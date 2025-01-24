package com.rockstock.backend.infrastructure.usecase.address.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressRequestDTO {

    private String addressLine;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private boolean isPrimary;
}
