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
import org.springframework.data.redis.core.RedisTemplate;
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
        private final RedisTemplate<String, String> redisTemplate;

        private static final String LOCK_KEY = "lock:warehouseStock:";

        public void releaseLockedStockForOrder(Long orderId) {
            orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);

            for (OrderItem orderItem : orderItems) {
                Product product = orderItem.getProduct();

                List<Warehouse> warehouses = warehouseRepository.findAll();

                for (Warehouse warehouse : warehouses) {
                    Optional<WarehouseStock> stockOpt = warehouseStockRepository.findByProductAndWarehouse(product, warehouse);
                    if (stockOpt.isEmpty()) {
                        continue;
                    }
                    WarehouseStock stock = stockOpt.get();
                    String orderLockKey = LOCK_KEY + stock.getId() + ":" + orderId;
                    String lockedQtyStr = redisTemplate.opsForValue().get(orderLockKey);
                    if (lockedQtyStr != null) {
                        long lockedQty = Long.parseLong(lockedQtyStr);
                        redisTemplate.delete(orderLockKey);

                        long newLockedQty = stock.getLockedQuantity() - lockedQty;
                        if (newLockedQty < 0) {
                            newLockedQty = 0;
                        }
                        stock.setLockedQuantity(newLockedQty);
                        warehouseStockRepository.save(stock);
                    }
                }
            }
        }
    }