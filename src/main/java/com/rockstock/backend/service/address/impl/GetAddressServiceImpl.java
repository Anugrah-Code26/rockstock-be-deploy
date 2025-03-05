package com.rockstock.backend.service.address.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.geolocation.Address;
import com.rockstock.backend.infrastructure.address.dto.GetAddressResponseDTO;
import com.rockstock.backend.infrastructure.address.repository.AddressRepository;
import com.rockstock.backend.service.address.GetAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAddressServiceImpl implements GetAddressService {

    private final AddressRepository addressRepository;

    private GetAddressResponseDTO mapToDTO(Address address) {
        return new GetAddressResponseDTO(address);
    }

    @Override
    @Transactional
    public List<Address> getUserAddresses(Long userId) {
        List<Address> userAddresses = addressRepository.findByUserId(userId);
        if (userAddresses.isEmpty()){
            throw new DataNotFoundException("Address not found !");
        }
        return userAddresses;
    }

    @Override
    @Transactional
    public Optional<Address> getUserAddressesByAddressId(Long userId, Long addressId) {
        Optional<Address> userAddress = addressRepository.findByUserIdAndAddressId(userId, addressId);
        if (userAddress.isEmpty()){
            throw new DataNotFoundException("Address not found !");
        }
        return userAddress;
    }

    @Override
    @Transactional
    public Optional<Address> getUserMainAddresses(Long userId, boolean isMain) {
        Optional<Address> userMainAddress = addressRepository.findByMainAddressUser(userId, true);
        if (userMainAddress.isEmpty()){
            throw new DataNotFoundException("Main address not found ! or User still do not have any address !");
        }
        return userMainAddress;
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserIdAndProvinceId(Long userId, Long provinceId) {
        List<Address> addresses = addressRepository.findByUserIdAndProvinceId(userId, provinceId);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserIdAndProvinceName(Long userId, String name) {
        List<Address> addresses = addressRepository.findByUserIdAndProvinceName(userId, name);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserIdAndCityId(Long userId, Long cityId) {
        List<Address> addresses = addressRepository.findByUserIdAndCityId(userId, cityId);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserIdAndCityName(Long userId, String name) {
        List<Address> addresses = addressRepository.findByUserIdAndCityName(userId, name);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserIdAndDistrictId(Long userId, Long districtId) {
        List<Address> addresses = addressRepository.findByUserIdAndDistrictId(userId, districtId);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserIdAndDistrictName(Long userId, String name) {
        List<Address> addresses = addressRepository.findByUserIdAndDistrictName(userId, name);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserIdAndSubDistrictId(Long userId, Long subDistrictId) {
        List<Address> addresses = addressRepository.findByUserIdAndSubDistrictId(userId, subDistrictId);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAddressesByUserIdAndSubDistrictName(Long userId, String name) {
        List<Address> addresses = addressRepository.findByUserIdAndSubDistrictName(userId, name);
        if (addresses.isEmpty()) throw new DataNotFoundException("Address not found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<GetAddressResponseDTO> getAddressByUserIdAndAddressId(Long userId, Long addressId) {
        return addressRepository.findByUserIdAndAddressId(userId, addressId)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public Optional<GetAddressResponseDTO> getAddressByUserIdAndLabel(Long userId, String label) {
        return addressRepository.findByUserIdAndLabel(userId, label)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public Optional<GetAddressResponseDTO> getMainAddressByUserId(Long userId) {
        return addressRepository.findByUserIdAndIsMainTrue(userId)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public List<GetAddressResponseDTO> getAllDeletedAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findAllDeletedAddressesByUserId(userId);
        if (addresses.isEmpty()) throw new DataNotFoundException("No deleted addresses found!");
        return addresses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }
}
