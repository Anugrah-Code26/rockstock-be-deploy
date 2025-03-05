package com.rockstock.backend.infrastructure.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseAdminResponseDTO {
    private Long id;
    private Long userId;
    private String fullname;
    private String email;
    private Long warehouseId;   // Tambahkan ini
    private String warehouseName;  // Tambahkan ini
}