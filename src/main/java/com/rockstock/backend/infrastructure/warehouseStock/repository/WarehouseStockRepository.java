package com.rockstock.backend.infrastructure.warehouseStock.repository;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.stock.WarehouseStock;
import com.rockstock.backend.entity.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long>, JpaSpecificationExecutor<WarehouseStock> {
    @Query("SELECT ws FROM WarehouseStock ws WHERE ws.product = :product AND ws.warehouse = :warehouse AND ws.deletedAt IS NULL")
    Optional<WarehouseStock> findByProductAndWarehouse(@Param("product") Product product, @Param("warehouse") Warehouse warehouse);

    @Modifying
    @Query("UPDATE WarehouseStock ws SET ws.deletedAt = :deletedAt WHERE ws.Id = :stockId AND ws.deletedAt IS NULL")
    int softDelete(@Param("stockId") Long stockId, @Param("deletedAt") OffsetDateTime deletedAt);

    @Modifying
    @Query("UPDATE WarehouseStock ws SET ws.deletedAt = NULL WHERE ws.Id = :stockId AND ws.deletedAt IS NOT NULL")
    int restore(@Param("stockId") Long stockId);

    @Query("SELECT ws FROM WarehouseStock ws WHERE ws.product.id = :productId AND ws.deletedAt IS NULL")
    List<WarehouseStock> findAllByProductId(@Param("productId") Long productId);

    @Query("SELECT ws FROM WarehouseStock ws WHERE ws.product.id = :productId AND ws.deletedAt IS NOT NULL")
    List<WarehouseStock> findByProductIdAndDeletedAtIsNotNull(@Param("productId") Long productId);

    @Query("SELECT ws FROM WarehouseStock ws WHERE ws.id = :stockId AND ws.deletedAt IS NULL")
    Optional<WarehouseStock> findByIdAndDeletedAtIsNull(@Param("stockId") Long stockId);

    @Query("SELECT COUNT(ws) > 0 FROM WarehouseStock ws WHERE ws.product.id = :productId AND ws.warehouse.id = :warehouseId AND ws.deletedAt IS NULL")
    boolean existsByProductAndWarehouse(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);

    @Query("SELECT COALESCE(SUM(ws.stockQuantity), 0) FROM WarehouseStock ws WHERE ws.product.id = :productId AND ws.deletedAt IS NULL")
    BigDecimal getTotalStockByProductId(@Param("productId") Long productId);
}