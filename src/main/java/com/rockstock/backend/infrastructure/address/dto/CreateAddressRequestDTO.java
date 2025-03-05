package com.rockstock.backend.infrastructure.address.dto;

import com.rockstock.backend.entity.geolocation.Address;
import com.rockstock.backend.entity.geolocation.City;
import com.rockstock.backend.entity.geolocation.SubDistrict;
import com.rockstock.backend.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequestDTO {

    @NotBlank(message = "Label is required")
    private String label;

    @NotBlank(message = "Address detail is required")
    private String addressDetail;

    @NotBlank(message = "Longitude is required")
    private String longitude;

    @NotBlank(message = "Latitude is required")
    private String latitude;

    private String note;

    private Boolean isMain = false;

    @NotNull
    private Long userId;

    @NotNull
    private Long subDistrictId;

    public Address toEntity(User user, SubDistrict subDistrict) {
        Address address = new Address();

        address.setLabel(label);
        address.setAddressDetail(addressDetail);
        address.setLongitude(longitude);
        address.setLatitude(latitude);
        address.setNote(note);
        address.setIsMain(isMain);
        address.setUser(user);
        address.setSubDistrict(subDistrict);

        return address;
    }
}
