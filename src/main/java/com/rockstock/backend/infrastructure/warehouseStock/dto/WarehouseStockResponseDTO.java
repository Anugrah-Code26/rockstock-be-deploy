package com.rockstock.backend.infrastructure.warehouseStock.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WarehouseStockResponseDTO {
    private Long stockId;
    private Long stockQuantity;
    private Long lockedQuantity;
    private String productName;
    private Long productId;
    private String warehouseName;
    private Long warehouseId;

    @Getter(AccessLevel.NONE) // Prevent Lombok from generating a getter
    private Long availableQuantity;

    public Long getAvailableQuantity() {
        long stock = (stockQuantity != null ? stockQuantity : 0L);
        long locked = (lockedQuantity != null ? lockedQuantity : 0L);
        return stock - locked;
    }
}