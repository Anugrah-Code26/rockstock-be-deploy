package com.rockstock.backend.infrastructure.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String longitude;
    private String latitude;

}
