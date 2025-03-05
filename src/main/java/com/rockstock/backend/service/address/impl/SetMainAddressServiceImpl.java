package com.rockstock.backend.service.address.impl;

import com.rockstock.backend.entity.geolocation.Address;
import com.rockstock.backend.infrastructure.address.repository.AddressRepository;
import com.rockstock.backend.service.address.SetMainAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetMainAddressServiceImpl implements SetMainAddressService {

    private final AddressRepository addressRepository;

    public SetMainAddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public void setMainAddress(Long userId, Long addressId) {
        // Cek apakah addressId valid untuk userId tertentu
        Address mainAddress = addressRepository.findByUserIdAndAddressId(userId, addressId)
                .orElseThrow(() -> new RuntimeException("Address not found for this user"));

        // Reset semua alamat lain ke isMain = false
        addressRepository.resetMainAddress(userId);

        // Set alamat yang dipilih sebagai isMain = true
        mainAddress.setIsMain(true);
        addressRepository.save(mainAddress);
    }
}
