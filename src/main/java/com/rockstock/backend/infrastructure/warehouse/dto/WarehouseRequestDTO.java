package com.rockstock.backend.infrastructure.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseRequestDTO {

    @NotBlank(message = "Warehouse name is required")
    private String name;

    @NotBlank
    private String address;

    @NotBlank(message = "Latitude is required")
    private String latitude;

    @NotBlank(message = "Longitude is required")
    private String longitude;

    @NotNull
    private Long subDistrictId;
}
