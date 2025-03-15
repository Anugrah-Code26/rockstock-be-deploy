package com.rockstock.backend.infrastructure.order.repository;

import com.rockstock.backend.entity.order.Order;
import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.SalesReportDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SalesReportRepository extends JpaRepository<Order, Long> {

    @Query("SELECT new com.rockstock.backend.infrastructure.order.dto.SalesReportDTO( " +
            "oi.order.warehouse.id, oi.order.warehouse.name, " +
            "oi.product.id, oi.product.productName, " +
            "oi.product.productCategory.id, oi.product.productCategory.categoryName, " +
            "CAST(SUM(oi.quantity) AS integer), CAST(SUM(oi.price * oi.quantity) AS BigDecimal), oi.order.createdAt) " +
            "FROM OrderItem oi " +
            "WHERE oi.order.status = :status " +
            "AND oi.order.createdAt BETWEEN :startDate AND :endDate " +
            "AND (:warehouseId IS NULL OR oi.order.warehouse.id = :warehouseId) " +
            "AND (:productId IS NULL OR oi.product.id = :productId) " +
            "AND (:productCategoryId IS NULL OR oi.product.productCategory.id = :productCategoryId) " +
            "GROUP BY oi.order.warehouse.id, oi.order.warehouse.name, " +
            "oi.product.id, oi.product.productName, " +
            "oi.product.productCategory.id, oi.product.productCategory.categoryName, " +
            "oi.order.createdAt")
    List<SalesReportDTO> findSalesReport(
            @Param("status") OrderStatusList status,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId,
            @Param("productCategoryId") Long productCategoryId
    );
}
