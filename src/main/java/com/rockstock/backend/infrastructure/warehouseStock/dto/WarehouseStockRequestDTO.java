package com.rockstock.backend.infrastructure.warehouseStock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WarehouseStockRequestDTO {
    @NotNull
    private Long productId;

    @NotNull
    private Long warehouseId;
}