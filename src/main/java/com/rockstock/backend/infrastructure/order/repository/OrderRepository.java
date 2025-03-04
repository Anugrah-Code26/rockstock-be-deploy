package com.rockstock.backend.infrastructure.order.repository;

import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :time")
    List<Order> findByStatusAndCreatedAtBefore(
            @Param("status") OrderStatusList status,
            @Param("time") OffsetDateTime time
    );

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.user " +
            "JOIN FETCH o.address a " +
            "JOIN FETCH a.subDistrict " +
            "JOIN FETCH o.warehouse " +
            "JOIN FETCH o.paymentMethod")
    List<Order> findAllWithDetails();

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findAllByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.warehouse.id = :warehouseId")
    List<Order> findAllByWarehouseId(Long warehouseId);

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findAllByStatus(OrderStatusList status);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findAllByUserIdAndStatus(Long userId, OrderStatusList status);

    @Query("SELECT o FROM Order o WHERE o.warehouse.id = :warehouseId AND o.status = :status")
    List<Order> findAllByWarehouseIdAndStatus(Long warehouseId, OrderStatusList status);

    @Query("SELECT o FROM Order o WHERE o.paymentMethod.name = :methodName")
    List<Order> findAllByPaymentMethodName(String methodName);

    @Query("SELECT o FROM Order o WHERE o.orderCode = :orderCode")
    Optional<Order> findByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    @Query("SELECT o FROM Order o WHERE o.orderCode = :orderCode AND o.user.id = :userId")
    Optional<Order> findByOrderCodeAndUserId(String orderCode, Long userId);
}
