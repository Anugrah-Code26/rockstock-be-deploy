package com.rockstock.backend.service.address;

import com.rockstock.backend.infrastructure.address.dto.GetAddressResponseDTO;

import java.util.List;
import java.util.Optional;

public interface GetAddressService {
    List<GetAddressResponseDTO> getAddressesByUserId(Long userId);
    List<GetAddressResponseDTO> getAddressesByUserIdAndProvinceId(Long userId, Long provinceId);
    List<GetAddressResponseDTO> getAddressesByUserIdAndProvinceName(Long userId, String name);
    List<GetAddressResponseDTO> getAddressesByUserIdAndCityId(Long userId, Long cityId);
    List<GetAddressResponseDTO> getAddressesByUserIdAndCityName(Long userId, String name);
    List<GetAddressResponseDTO> getAddressesByUserIdAndDistrictId(Long userId, Long districtId);
    List<GetAddressResponseDTO> getAddressesByUserIdAndDistrictName(Long userId, String name);
    List<GetAddressResponseDTO> getAddressesByUserIdAndSubDistrictId(Long userId, Long subDistrictId);
    List<GetAddressResponseDTO> getAddressesByUserIdAndSubDistrictName(Long userId, String name);
    Optional<GetAddressResponseDTO> getAddressByUserIdAndAddressId(Long userId, Long addressId);
    Optional<GetAddressResponseDTO> getAddressByUserIdAndLabel(Long userId, String label);
    Optional<GetAddressResponseDTO> getMainAddressByUserId(Long userId);
    List<GetAddressResponseDTO> getAllDeletedAddressesByUserId(Long userId);
}
