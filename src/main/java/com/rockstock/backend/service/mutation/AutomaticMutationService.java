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
        System.out.println("Starting transfer for order ID: " + orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        System.out.println("Order found: " + order);

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
        System.out.println("Order contains " + orderItems.size() + " items");

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            long requiredQty = orderItem.getQuantity();
            Warehouse destinationWarehouse = order.getWarehouse();

            System.out.println("Processing product: " + product.getProductName() + " | Required Qty: " + requiredQty);
            System.out.println("Destination Warehouse: " + destinationWarehouse.getName());

            // Check if the destination warehouse has locked stock first
            Optional<WarehouseStock> receiverStockOpt = warehouseStockRepository.findByProductAndWarehouse(product, destinationWarehouse);
            if (receiverStockOpt.isEmpty()) {
                receiverStockOpt = Optional.of(new WarehouseStock());
            }

            WarehouseStock receiverStock = receiverStockOpt.get();
            String destinationOrderLockKey = String.format("o:%d:p:%d", orderId, receiverStock.getProduct().getId());
            String destinationLockedQtyStr = redisTemplate.opsForValue().get(destinationOrderLockKey);
            long destinationLockedQty = (destinationLockedQtyStr != null) ? Long.parseLong(destinationLockedQtyStr) : 0L;

            // If there's stock at the destination warehouse, lock it first
            if (destinationLockedQty > 0) {
                long transferQtyFromDestination = Math.min(requiredQty, destinationLockedQty);
                requiredQty -= transferQtyFromDestination;
                System.out.println("Transferring " + transferQtyFromDestination + " units from destination warehouse");

                // Perform stock transfer to destination warehouse (from destination warehouse)
                long receiverPrevStock = receiverStock.getStockQuantity();
                long receiverNewStock = receiverPrevStock + transferQtyFromDestination;
                receiverStock.setStockQuantity(receiverNewStock);
                warehouseStockRepository.save(receiverStock);

                // Update Redis to lock the stock
                redisTemplate.opsForValue().set(destinationOrderLockKey, String.valueOf(receiverNewStock), 1, TimeUnit.HOURS);

                // Create receiver mutation journal (for destination warehouse)
                createMutationJournal(receiverStock, destinationWarehouse, destinationWarehouse, transferQtyFromDestination, receiverPrevStock, receiverNewStock, "Received " + transferQtyFromDestination + " units from " + destinationWarehouse.getName(), StockAdjustmentType.POSITIVE);

                // If there's still stock left to fulfill, look to nearest warehouses
                if (requiredQty > 0) {
                    List<Warehouse> sortedWarehouses = findWarehousesSortedByDistance(destinationWarehouse);
                    for (Warehouse senderWarehouse : sortedWarehouses) {
                        if (senderWarehouse.getId().equals(destinationWarehouse.getId())) continue;  // Skip if the sender is the same as the destination warehouse

                        Optional<WarehouseStock> senderStockOpt = warehouseStockRepository.findByProductAndWarehouse(product, senderWarehouse);
                        if (senderStockOpt.isEmpty()) {
                            System.out.println("No stock found in warehouse: " + senderWarehouse.getName());
                            continue;
                        }

                        WarehouseStock senderStock = senderStockOpt.get();
                        String orderLockKey = String.format("o:%d:p:%d", orderId, senderStock.getProduct().getId());

                        // Fetch locked stock from Redis
                        String lockedQtyStr = redisTemplate.opsForValue().get(orderLockKey);
                        long lockedQty = (lockedQtyStr != null && lockedQtyStr.matches("\\d+")) ? Long.parseLong(lockedQtyStr) : 0L;
                        System.out.println("Locked stock before transfer: " + lockedQty);

                        if (lockedQty == 0) {
                            System.out.println("Error: No locked stock available for order " + orderId + " and product " + product.getProductName());
                            continue;
                        }

                        // Perform transfer from sender warehouse
                        long quantityToTransfer = Math.min(requiredQty, lockedQty);
                        if (quantityToTransfer <= 0) continue;

                        System.out.println("Transferring " + quantityToTransfer + " units from " + senderWarehouse.getName() + " to " + destinationWarehouse.getName());

                        // Update sender and receiver stock
                        long senderPrevStock = senderStock.getStockQuantity();
                        long senderNewStock = senderPrevStock - quantityToTransfer;

                        // Fetch latest sender stock and update it
                        WarehouseStock latestSenderStock = warehouseStockRepository.findById(senderStock.getId()).orElse(senderStock);
                        long newLockedQty = Math.max(latestSenderStock.getLockedQuantity() - quantityToTransfer, 0);
                        latestSenderStock.setLockedQuantity(newLockedQty);
                        latestSenderStock.setStockQuantity(senderNewStock);
                        warehouseStockRepository.save(latestSenderStock);

                        System.out.println("Updated sender stock: Prev=" + senderPrevStock + ", New=" + senderNewStock);

                        // Create sender mutation journal (for sender warehouse)
                        createMutationJournal(latestSenderStock, senderWarehouse, destinationWarehouse, quantityToTransfer, senderPrevStock, senderNewStock, "Sent " + quantityToTransfer + " units to " + destinationWarehouse.getName(), StockAdjustmentType.NEGATIVE);

                        // Transfer stock to the destination warehouse
                        WarehouseStock receiverStockFinal = warehouseStockRepository.findByProductAndWarehouse(product, destinationWarehouse)
                                .orElseGet(() -> {
                                    WarehouseStock newStock = new WarehouseStock();
                                    newStock.setProduct(product);
                                    newStock.setWarehouse(destinationWarehouse);
                                    newStock.setStockQuantity(0L);
                                    newStock.setLockedQuantity(0L);
                                    return newStock;
                                });

                        long receiverPrevStockFinal = receiverStockFinal.getStockQuantity();
                        long receiverNewStockFinal = receiverPrevStockFinal + quantityToTransfer;
                        receiverStockFinal.setStockQuantity(receiverNewStockFinal);
                        warehouseStockRepository.save(receiverStockFinal);

                        // Lock transferred stock in Redis
                        redisTemplate.opsForValue().set(destinationOrderLockKey, String.valueOf(receiverNewStockFinal), 1, TimeUnit.HOURS);
                        requiredQty -= quantityToTransfer;
                    }
                }
            }

            // Check if there is still required quantity after all transfers
            if (requiredQty > 0) {
                throw new RuntimeException("Not enough locked stock available to transfer for product " + product.getProductName());
            }

            System.out.println("Stock transfer process completed for order ID: " + orderId);
        }
    }

    private void createMutationJournal(WarehouseStock warehouseStock, Warehouse senderWarehouse, Warehouse destinationWarehouse, long mutationQuantity, long previousStockQuantity, long newStockQuantity, String description, StockAdjustmentType adjustmentType) {
        MutationJournal journal = new MutationJournal();
        journal.setMutationQuantity(mutationQuantity);
        journal.setPreviousStockQuantity(previousStockQuantity);
        journal.setNewStockQuantity(newStockQuantity);
        journal.setDescription(description);
        journal.setWarehouseStock(warehouseStock);
        journal.setOriginWarehouse(senderWarehouse);
        journal.setDestinationWarehouse(destinationWarehouse);
        journal.setStockChangeType(StockChangeType.TRANSFER);
        journal.setStockAdjustmentType(adjustmentType);
        journal.setMutationStatus(MutationStatus.COMPLETED);

        // Save the journal (You can add logic to create related journals, if needed)
        mutationJournalRepository.save(journal);
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