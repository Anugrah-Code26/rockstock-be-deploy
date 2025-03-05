package com.rockstock.backend.infrastructure.warehouse.repository;

import com.rockstock.backend.entity.warehouse.WarehouseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseAdminRepository extends JpaRepository<WarehouseAdmin, Long> {
    List<WarehouseAdmin> findByWarehouseId(Long warehouseId);
}