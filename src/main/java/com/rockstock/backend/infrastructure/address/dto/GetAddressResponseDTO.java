package com.rockstock.backend.infrastructure.address.dto;

import com.rockstock.backend.entity.geolocation.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAddressResponseDTO {

    private Long addressId;
    private String label;
    private String addressDetail;
    private String longitude;
    private String latitude;
    private String note;
    private Boolean isMain;
    private Long userId;
    private Long subDistrictId;
    private String subDistrictName;
    private String addressPostalCode;
    private Long districtId;
    private String districtName;
    private Long cityId;
    private String cityName;
    private Long provinceId;
    private String provinceName;

    public GetAddressResponseDTO(Address address) {
        this.addressId = address.getId();
        this.label = address.getLabel();
        this.addressDetail = address.getAddressDetail();
        this.longitude = address.getLongitude();
        this.latitude = address.getLatitude();
        this.note = address.getNote();
        this.isMain = address.getIsMain();
        this.userId = address.getUser().getId();
        this.subDistrictId = address.getSubDistrict().getId();
        this.subDistrictName = address.getSubDistrict().getName();
        this.addressPostalCode = address.getSubDistrict().getPostalCode();
        this.districtId = address.getSubDistrict().getDistrict().getId();
        this.districtName = address.getSubDistrict().getDistrict().getName();
        this.cityId = address.getSubDistrict().getDistrict().getCity().getId();
        this.cityName = address.getSubDistrict().getDistrict().getCity().getName();
        this.provinceId = address.getSubDistrict().getDistrict().getCity().getProvince().getId();
        this.provinceName = address.getSubDistrict().getDistrict().getCity().getProvince().getName();
    }
}
