package com.rockstock.backend.infrastructure.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignWarehouseAdminDTO {
    @NotNull
    private Long userId;

    @NotNull
    private Long warehouseId;
}