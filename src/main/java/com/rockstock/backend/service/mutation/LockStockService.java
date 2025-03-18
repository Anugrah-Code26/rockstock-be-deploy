package com.rockstock.backend.service.mutation;

import com.rockstock.backend.common.utils.DistanceCalculator;
import com.rockstock.backend.entity.cart.Cart;
import com.rockstock.backend.entity.cart.CartItem;
import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.cart.repository.CartItemRepository;
import com.rockstock.backend.infrastructure.order.repository.OrderRepository;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.warehouseStock.repository.WarehouseStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LockStockService {
    private final CartItemRepository cartItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void lockStockForOrder(Long orderId, Cart cart) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new RuntimeException("Order not found"));

        List<CartItem> cartItems = cartItemRepository.findAllByActiveCartId(cart.getId());

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            long requiredQty = cartItem.getQuantity().longValue();
            Warehouse destinationWarehouse = order.getWarehouse();

            List<Warehouse> sortedWarehouses = findWarehousesSortedByDistance(destinationWarehouse);

            for (Warehouse warehouse : sortedWarehouses) {
                if (requiredQty <= 0) break;

                WarehouseStock warehouseStock = findAvailableStock(product, warehouse);

                if (warehouseStock != null) {
                    long availableStock = warehouseStock.getStockQuantity() - warehouseStock.getLockedQuantity();

                    if (availableStock > 0) {
                        long lockedQty = Math.min(availableStock, requiredQty);
                        lockStockInWarehouse(warehouseStock, lockedQty);
                        requiredQty -= lockedQty;
                    }
                }
            }

            if (requiredQty > 0) {
                throw new RuntimeException("Not enough stock available in any warehouse.");
            }
        }
    }

    private WarehouseStock findAvailableStock(Product product, Warehouse warehouse) {
        Optional<WarehouseStock> warehouseStockOpt = warehouseStockRepository.findByProductAndWarehouse(product, warehouse);
        return warehouseStockOpt.orElse(null);
    }

    private void lockStockInWarehouse(WarehouseStock warehouseStock, long qty) {
        System.out.println("Before locking, Locked Qty for product " + warehouseStock.getProduct().getProductName() +
                " in warehouse " + warehouseStock.getWarehouse().getName() + " is: " + warehouseStock.getLockedQuantity());

        long newLockedQty = warehouseStock.getLockedQuantity() + qty;
        warehouseStock.setLockedQuantity(newLockedQty);
        warehouseStockRepository.save(warehouseStock);

    }

    private List<Warehouse> findWarehousesSortedByDistance(Warehouse destinationWarehouse) {
        if (destinationWarehouse == null || destinationWarehouse.getLatitude() == null || destinationWarehouse.getLongitude() == null) {
            throw new IllegalArgumentException("Destination warehouse has invalid location data.");
        }

        double currentLat = Double.parseDouble(destinationWarehouse.getLatitude());
        double currentLng = Double.parseDouble(destinationWarehouse.getLongitude());

        List<Warehouse> warehouses = warehouseRepository.findAll();
        warehouses.sort((w1, w2) -> {
            double distanceToW1 = DistanceCalculator.haversine(currentLat, currentLng, Double.parseDouble(w1.getLatitude()), Double.parseDouble(w1.getLongitude()));
            double distanceToW2 = DistanceCalculator.haversine(currentLat, currentLng, Double.parseDouble(w2.getLatitude()), Double.parseDouble(w2.getLongitude()));
            return Double.compare(distanceToW1, distanceToW2);
        });

        return warehouses;
    }
}