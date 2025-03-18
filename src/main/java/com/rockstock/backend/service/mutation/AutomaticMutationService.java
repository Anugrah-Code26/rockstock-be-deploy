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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AutomaticMutationService {
    private final OrderItemRepository orderItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final OrderRepository orderRepository;
    private final MutationJournalRepository mutationJournalRepository;

    @Transactional
    public void transferLockedStockForOrder(Long orderId) {
        System.out.println("Starting transfer for order ID: " + orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        System.out.println("Order found: " + order);

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
        System.out.println("Order contains " + orderItems.size() + " items");

        Warehouse destinationWarehouse = order.getWarehouse();

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            long requiredQty = orderItem.getQuantity();

            System.out.println("Processing product: " + product.getProductName() + " | Required Qty: " + requiredQty);
            System.out.println("Destination Warehouse: " + destinationWarehouse.getName());

            // **Step 1: Use locked stock from the destination warehouse first**
            Optional<WarehouseStock> receiverStockOpt = warehouseStockRepository.findByProductAndWarehouse(product, destinationWarehouse);
            WarehouseStock receiverStock = receiverStockOpt.orElseGet(() -> {
                WarehouseStock newStock = new WarehouseStock();
                newStock.setProduct(product);
                newStock.setWarehouse(destinationWarehouse);
                newStock.setStockQuantity(0L);
                newStock.setLockedQuantity(0L);
                return newStock;
            });

            long destinationLockedQty = receiverStock.getLockedQuantity();

            if (destinationLockedQty > 0) {
                long usedQty = Math.min(requiredQty, destinationLockedQty);
                requiredQty -= usedQty;
                System.out.println("Using " + usedQty + " locked stock from destination warehouse");

                receiverStock.setStockQuantity(receiverStock.getStockQuantity() + usedQty);
                receiverStock.setLockedQuantity(receiverStock.getLockedQuantity() - usedQty);
                warehouseStockRepository.save(receiverStock);

                // Log mutation as receiver (destination warehouse receives its own stock)
                createMutationJournal(receiverStock, destinationWarehouse, destinationWarehouse, usedQty, receiverStock.getStockQuantity() - usedQty, receiverStock.getStockQuantity(), "Used " + usedQty + " locked stock from own warehouse", StockAdjustmentType.POSITIVE);
            }

            // **Step 2: If more stock is needed, transfer from the nearest warehouses**
            if (requiredQty > 0) {
                List<Warehouse> sortedWarehouses = findWarehousesSortedByDistance(destinationWarehouse);
                for (Warehouse senderWarehouse : sortedWarehouses) {
                    if (senderWarehouse.getId().equals(destinationWarehouse.getId())) continue; // Skip self

                    Optional<WarehouseStock> senderStockOpt = warehouseStockRepository.findByProductAndWarehouse(product, senderWarehouse);
                    if (senderStockOpt.isEmpty()) {
                        System.out.println("No stock found in warehouse: " + senderWarehouse.getName());
                        continue;
                    }

                    WarehouseStock senderStock = senderStockOpt.get();
                    long senderLockedQty = senderStock.getLockedQuantity();

                    if (senderLockedQty == 0) {
                        System.out.println("No locked stock available in " + senderWarehouse.getName());
                        continue;
                    }

                    long transferQty = Math.min(requiredQty, senderLockedQty);
                    if (transferQty <= 0) continue;

                    System.out.println("Transferring " + transferQty + " units from " + senderWarehouse.getName() + " to " + destinationWarehouse.getName());

                    // **Update sender warehouse stock**
                    long senderPrevStock = senderStock.getStockQuantity();
                    long senderNewStock = senderPrevStock - transferQty;
                    senderStock.setStockQuantity(senderNewStock);
                    senderStock.setLockedQuantity(senderStock.getLockedQuantity() - transferQty);
                    warehouseStockRepository.save(senderStock);

                    // Log mutation as sender (warehouse sending stock)
                    createMutationJournal(senderStock, senderWarehouse, destinationWarehouse, transferQty, senderPrevStock, senderNewStock, "Sent " + transferQty + " units to " + destinationWarehouse.getName(), StockAdjustmentType.NEGATIVE);

                    // **Update receiver warehouse stock (Destination Warehouse)**
                    long receiverPrevStock = receiverStock.getStockQuantity();
                    long receiverNewStock = receiverPrevStock + transferQty;
                    receiverStock.setStockQuantity(receiverNewStock);
                    receiverStock.setLockedQuantity(receiverStock.getLockedQuantity() + transferQty); // Add locked stock
                    warehouseStockRepository.save(receiverStock);

                    // Log mutation as receiver (destination warehouse receives stock)
                    createMutationJournal(receiverStock, senderWarehouse, destinationWarehouse, transferQty, receiverPrevStock, receiverNewStock, "Received " + transferQty + " units from " + senderWarehouse.getName(), StockAdjustmentType.POSITIVE);

                    requiredQty -= transferQty;

                    if (requiredQty == 0) break; // Stop when required quantity is met
                }
            }

            // **Step 3: If stock is still insufficient, throw an error**
            if (requiredQty > 0) {
                throw new RuntimeException("Not enough locked stock available to transfer for product " + product.getProductName());
            }

            System.out.println("Stock transfer completed for order ID: " + orderId);
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