package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderItem;
import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.*;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.mutationJournal.repository.MutationJournalRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderItemRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DestinationShipmentService {

    private final OrderItemRepository orderItemRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final MutationJournalRepository mutationJournalRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_KEY = "lock:warehouseStock:";

    public void shipOrder(Order order) {
        Warehouse destinationWarehouse = order.getWarehouse();
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            long requiredQty = orderItem.getQuantity();

            Optional<WarehouseStock> destinationStockOpt = warehouseStockRepository.findByProductAndWarehouse(product, destinationWarehouse);
            if (destinationStockOpt.isEmpty()) {
                throw new RuntimeException("No stock record found for product " + product.getProductName() +
                        " in destination warehouse " + destinationWarehouse.getName());
            }
            WarehouseStock destinationStock = destinationStockOpt.get();

            String destinationOrderLockKey = LOCK_KEY + destinationStock.getId() + ":" + order.getId();
            String lockedQtyStr = redisTemplate.opsForValue().get(destinationOrderLockKey);
            long lockedQty = (lockedQtyStr != null) ? Long.parseLong(lockedQtyStr) : 0L;

            if (lockedQty < requiredQty) {
                throw new RuntimeException("Insufficient locked quantity (" + lockedQty + ") for product "
                        + product.getProductName() + " in order " + order.getOrderCode());
            }

            synchronized (this) { // Prevent race conditions
                long destinationPrevStock = destinationStock.getStockQuantity();
                long destinationPrevLocked = destinationStock.getLockedQuantity();

                long destinationNewStock = destinationPrevStock - requiredQty;
                long destinationNewLocked = destinationPrevLocked - requiredQty;

                if (destinationNewStock < 0 || destinationNewLocked < 0) {
                    throw new RuntimeException("Stock inconsistency detected during shipping for " + product.getProductName());
                }

                destinationStock.setStockQuantity(destinationNewStock);
                destinationStock.setLockedQuantity(destinationNewLocked);
                warehouseStockRepository.save(destinationStock);

                redisTemplate.delete(destinationOrderLockKey);

                MutationJournal shipmentJournal = new MutationJournal();
                shipmentJournal.setMutationQuantity(requiredQty);
                shipmentJournal.setPreviousStockQuantity(destinationPrevStock);
                shipmentJournal.setNewStockQuantity(destinationNewStock);
                shipmentJournal.setDescription("Destination warehouse " + destinationWarehouse.getName() +
                        " shipped " + requiredQty + " units of " + product.getProductName() +
                        " to customer for order " + order.getOrderCode());
                shipmentJournal.setWarehouseStock(destinationStock);
                shipmentJournal.setOriginWarehouse(destinationWarehouse);
                shipmentJournal.setDestinationWarehouse(null);
                shipmentJournal.setStockChangeType(StockChangeType.SALES_DISPATCHED);
                shipmentJournal.setStockAdjustmentType(StockAdjustmentType.NEGATIVE);
                shipmentJournal.setMutationStatus(MutationStatus.ON_DELIVERY);
                mutationJournalRepository.save(shipmentJournal);
            }
        }
    }
}