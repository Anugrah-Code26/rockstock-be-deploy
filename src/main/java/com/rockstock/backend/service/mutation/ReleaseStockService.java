package com.rockstock.backend.service.mutation;

import com.rockstock.backend.entity.order.OrderItem;
import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.order.repository.OrderItemRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReleaseStockService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    public void releaseLockedStockForOrder(Long orderId) {
        // Check if order exists
        orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Get all items in the order
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            Long releasedQty = orderItem.getQuantity().longValue();

            // Find stock in warehouses
            List<Warehouse> warehouses = warehouseRepository.findAll();
            for (Warehouse warehouse : warehouses) {
                Optional<WarehouseStock> stockOpt = warehouseStockRepository.findByProductAndWarehouse(product, warehouse);

                if (stockOpt.isPresent()) {
                    WarehouseStock stock = stockOpt.get();

                    // Reduce locked quantity
                    long newLockedQty = Math.max(stock.getLockedQuantity() - releasedQty, 0);
                    stock.setLockedQuantity(newLockedQty);
                    warehouseStockRepository.save(stock);

                    // Stop looping if all locked stock is released
                    if (newLockedQty == 0) {
                        break;
                    }
                }
            }
        }
    }
}