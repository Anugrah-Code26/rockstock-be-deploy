package com.rockstock.backend.infrastructure.warehouseStock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WarehouseStockResponseDTO {
    private Long stockId;
    private Long stockQuantity;
    private String productName;
    private Long productId;
    private String warehouseName;
    private Long warehouseId;
}