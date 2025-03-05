package com.rockstock.backend.infrastructure.geolocation.dto;

import com.rockstock.backend.entity.geolocation.Province;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProvinceResponseDTO {

    private Long id;
    private String name;

    public GetProvinceResponseDTO(Province province) {
        this.id = province.getId();
        this.name = province.getName();
    }
}
