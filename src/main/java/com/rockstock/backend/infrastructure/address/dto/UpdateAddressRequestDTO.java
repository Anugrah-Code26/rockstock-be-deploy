package com.rockstock.backend.infrastructure.address.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequestDTO {

    private String label;
    private String addressDetail;
    private String longitude;
    private String latitude;
    private String note;
    private Boolean isMain;
    private Long subDistrictId;
}
