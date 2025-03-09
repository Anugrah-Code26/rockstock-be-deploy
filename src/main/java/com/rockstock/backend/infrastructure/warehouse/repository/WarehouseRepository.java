package com.rockstock.backend.infrastructure.warehouse.repository;

import com.rockstock.backend.entity.warehouse.Warehouse;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findById(Long id);
    boolean existsById(Long id);

    @Query("SELECT p FROM Warehouse p WHERE p.id = :warehouseId AND p.deletedAt IS NULL")
    Optional<Warehouse> findByIdAndDeletedAtIsNull(@Param("warehouseId") Long warehouseId);

    @Query("SELECT w FROM Warehouse w WHERE w.id IN :warehouseIds")
    List<Warehouse> findByIds(@Param("warehouseIds") List<Long> warehouseIds);
}
