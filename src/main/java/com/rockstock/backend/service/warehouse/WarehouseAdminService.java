package com.rockstock.backend.service.warehouse;

import com.rockstock.backend.infrastructure.warehouse.dto.AssignWarehouseAdminDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseAdminResponseDTO;

import java.util.List;

public interface WarehouseAdminService {
    void assignWarehouseAdmin(AssignWarehouseAdminDTO request, String currentUserRole);
    void removeWarehouseAdmin(Long warehouseAdminId);
    List<WarehouseAdminResponseDTO> getWarehouseAdmins(Long warehouseId);
    List<WarehouseAdminResponseDTO> getAllWarehouseAdmins();
}
