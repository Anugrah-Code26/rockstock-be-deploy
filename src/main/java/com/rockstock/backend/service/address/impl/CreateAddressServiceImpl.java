package com.rockstock.backend.service.address.impl;

import com.rockstock.backend.common.exceptions.DuplicateDataException;
import com.rockstock.backend.entity.geolocation.Address;
import com.rockstock.backend.entity.geolocation.SubDistrict;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.address.dto.CreateAddressRequestDTO;
import com.rockstock.backend.infrastructure.address.repository.AddressRepository;
import com.rockstock.backend.infrastructure.geolocation.repository.SubDistrictRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.address.CreateAddressService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;


import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CreateAddressServiceImpl implements CreateAddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final SubDistrictRepository subDistrictRepository;

    @Override
    @Transactional
    public Address createAddress(CreateAddressRequestDTO req) {
        Optional<Address> existingLabelAddress = addressRepository.findByUserIdAndLabel(req.getUserId(), req.getLabel());
        if (existingLabelAddress.isPresent()) {
            throw new DuplicateDataException("Label is already exist !");
        }

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        SubDistrict subDistrict = subDistrictRepository.findById(req.getSubDistrictId())
                .orElseThrow(() -> new RuntimeException("Sub-District not found"));

        Address newAddress = new Address();
        newAddress.setUser(user);
        newAddress.setSubDistrict(subDistrict);
        newAddress.setLabel(req.getLabel());
        newAddress.setAddressDetail(req.getAddressDetail());
        newAddress.setLongitude(req.getLongitude());
        newAddress.setLatitude(req.getLatitude());
        newAddress.setNote(req.getNote());

        // Pastikan tanggal tidak null
        newAddress.setCreatedAt(OffsetDateTime.now());
        newAddress.setUpdatedAt(OffsetDateTime.now());

        List<Address> checkAddress = addressRepository.findByUserId(req.getUserId());
        if (checkAddress.isEmpty()) {
            newAddress.setIsMain(true);
        } else {
            newAddress.setIsMain(false); // Set nilai isMain secara eksplisit
        }
        try {
            return addressRepository.save(newAddress);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while saving address: " + e.getMessage());
        }
    }

}
