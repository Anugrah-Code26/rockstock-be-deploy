package com.rockstock.backend.infrastructure.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseRequestDTO {
    @NotNull
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String longitude;

    @NotNull
    private String latitude;

    @NotNull
    private Long cityId;
}
