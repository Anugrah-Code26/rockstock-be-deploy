package com.rockstock.backend.infrastructure.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private Long subDistrictId;
    private String subDistrictPostalCode;
}
