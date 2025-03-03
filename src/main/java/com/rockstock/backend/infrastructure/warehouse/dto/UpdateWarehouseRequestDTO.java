package com.rockstock.backend.infrastructure.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWarehouseRequestDTO {
    @NotNull
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String longitude;

    @NotNull
    private String latitude;
}