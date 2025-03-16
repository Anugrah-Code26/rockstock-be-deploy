package com.rockstock.backend.service.mutation;

import com.rockstock.backend.common.utils.DistanceCalculator;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderItem;
import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.*;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderItemRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class AutomaticMutationService {

    private final OrderItemRepository orderItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final OrderRepository orderRepository;
    private final MutationJournalRepository mutationJournalRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void transferLockedStockForOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            long requiredQty = orderItem.getQuantity();
            Warehouse destinationWarehouse = order.getWarehouse();

            List<Warehouse> sortedWarehouses = findWarehousesSortedByDistance(destinationWarehouse);

            for (Warehouse senderWarehouse : sortedWarehouses) {
                if (senderWarehouse.getId().equals(destinationWarehouse.getId())) continue;
                if (requiredQty <= 0) break;

                Optional<WarehouseStock> senderStockOpt = warehouseStockRepository.findByProductAndWarehouse(product, senderWarehouse);
                if (senderStockOpt.isEmpty()) continue;

                WarehouseStock senderStock = senderStockOpt.get();
                String orderLockKey = String.format("o:%d:p:%d", orderId, senderStock.getProduct().getId());

                // Fetch locked stock from Redis
                String lockedQtyStr = redisTemplate.opsForValue().get(orderLockKey);
                long lockedQty = (lockedQtyStr != null && lockedQtyStr.matches("\\d+")) ? Long.parseLong(lockedQtyStr) : 0L;

                // Unlock the stock at the sender warehouse
                redisTemplate.delete(orderLockKey);

                long quantityToTransfer = Math.min(requiredQty, lockedQty);
                if (quantityToTransfer <= 0) continue;

                long senderPrevStock = senderStock.getStockQuantity();
                long senderNewStock = senderPrevStock - quantityToTransfer;

                // Fetch latest sender stock and update it
                WarehouseStock latestSenderStock = warehouseStockRepository.findById(senderStock.getId()).orElse(senderStock);
                long newLockedQty = Math.max(latestSenderStock.getLockedQuantity() - quantityToTransfer, 0);
                latestSenderStock.setLockedQuantity(newLockedQty);
                latestSenderStock.setStockQuantity(senderNewStock);
                warehouseStockRepository.save(latestSenderStock);

                // Transfer stock to the destination warehouse
                WarehouseStock receiverStock = warehouseStockRepository.findByProductAndWarehouse(product, destinationWarehouse)
                        .orElseGet(() -> {
                            WarehouseStock newStock = new WarehouseStock();
                            newStock.setProduct(product);
                            newStock.setWarehouse(destinationWarehouse);
                            newStock.setStockQuantity(0L);
                            newStock.setLockedQuantity(0L);
                            return newStock;
                        });

                long receiverPrevStock = receiverStock.getStockQuantity();
                long receiverNewStock = receiverPrevStock + quantityToTransfer;
                receiverStock.setStockQuantity(receiverNewStock);
                warehouseStockRepository.save(receiverStock);

                // Lock the transferred stock in the destination warehouse
                String destinationOrderLockKey = String.format("o:%d:p:%d", orderId, receiverStock.getProduct().getId());
                String destinationLockedQtyStr = redisTemplate.opsForValue().get(destinationOrderLockKey);
                long destinationOrderLockedQty = (destinationLockedQtyStr != null) ? Long.parseLong(destinationLockedQtyStr) : 0L;
                long newDestinationLock = destinationOrderLockedQty + quantityToTransfer;
                redisTemplate.opsForValue().set(destinationOrderLockKey, String.valueOf(newDestinationLock), 1, TimeUnit.HOURS);

                // Create sender journal entry
                MutationJournal senderJournal = new MutationJournal();
                senderJournal.setMutationQuantity(quantityToTransfer);
                senderJournal.setPreviousStockQuantity(senderPrevStock);
                senderJournal.setNewStockQuantity(senderNewStock);
                senderJournal.setDescription("Auto mutation transfer " + quantityToTransfer
                        + " of " + product.getProductName() + " in " + senderWarehouse.getName());
                senderJournal.setWarehouseStock(senderStock);
                senderJournal.setOriginWarehouse(senderWarehouse);
                senderJournal.setDestinationWarehouse(destinationWarehouse);
                senderJournal.setStockChangeType(StockChangeType.TRANSFER);
                senderJournal.setStockAdjustmentType(StockAdjustmentType.NEGATIVE);
                senderJournal.setMutationStatus(MutationStatus.COMPLETED);

                // Create receiver journal entry
                MutationJournal receiverJournal = new MutationJournal();
                receiverJournal.setMutationQuantity(quantityToTransfer);
                receiverJournal.setPreviousStockQuantity(receiverPrevStock);
                receiverJournal.setNewStockQuantity(receiverNewStock);
                receiverJournal.setDescription("Auto mutation receive " + quantityToTransfer
                        + " of " + product.getProductName() + " in " + destinationWarehouse.getName());
                receiverJournal.setWarehouseStock(receiverStock);
                receiverJournal.setOriginWarehouse(senderWarehouse);
                receiverJournal.setDestinationWarehouse(destinationWarehouse);
                receiverJournal.setStockChangeType(StockChangeType.TRANSFER);
                receiverJournal.setStockAdjustmentType(StockAdjustmentType.POSITIVE);
                receiverJournal.setMutationStatus(MutationStatus.COMPLETED);
                receiverJournal.setRelatedJournal(senderJournal);

                // Save both journals in a single transaction
                mutationJournalRepository.saveAll(List.of(senderJournal, receiverJournal));

                requiredQty -= quantityToTransfer;
            }

            if (requiredQty > 0) {
                throw new RuntimeException("Not enough locked stock available to transfer for product " + product.getProductName());
            }
        }
    }

    private List<Warehouse> findWarehousesSortedByDistance(Warehouse destinationWarehouse) {
        if (destinationWarehouse == null || destinationWarehouse.getLatitude() == null || destinationWarehouse.getLongitude() == null) {
            throw new IllegalArgumentException("Destination warehouse has invalid location data.");
        }
        double currentLat = Double.parseDouble(destinationWarehouse.getLatitude());
        double currentLng = Double.parseDouble(destinationWarehouse.getLongitude());
        List<Warehouse> warehouses = warehouseRepository.findAll();
        warehouses.sort((w1, w2) -> {
            double distanceToW1 = DistanceCalculator.haversine(currentLat, currentLng,
                    Double.parseDouble(w1.getLatitude()), Double.parseDouble(w1.getLongitude()));
            double distanceToW2 = DistanceCalculator.haversine(currentLat, currentLng,
                    Double.parseDouble(w2.getLatitude()), Double.parseDouble(w2.getLongitude()));
            return Double.compare(distanceToW1, distanceToW2);
        });
        return warehouses;
    }
}