package com.rockstock.backend.service.warehouse;

import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseResponseDTO;

import java.util.List;

public interface WarehouseService {
    WarehouseResponseDTO createWarehouse(WarehouseRequestDTO request);
    List<WarehouseResponseDTO> getAllWarehouses();
    WarehouseResponseDTO updateWarehouse(Long warehouseId, WarehouseRequestDTO request);
    void deleteWarehouse(Long warehouseId);
}