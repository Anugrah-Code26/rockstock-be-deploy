package com.rockstock.backend.service.address.impl;

import com.rockstock.backend.common.exceptions.DataNotFoundException;
import com.rockstock.backend.entity.geolocation.Address;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.infrastructure.address.dto.CreateAddressRequestDTO;
import com.rockstock.backend.infrastructure.address.dto.GetAddressResponseDTO;
import com.rockstock.backend.infrastructure.address.dto.UpdateAddressRequestDTO;
import com.rockstock.backend.infrastructure.address.repository.AddressRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import com.rockstock.backend.service.address.DeleteAddressService;
import com.rockstock.backend.service.address.GetAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeleteAddressServiceImpl implements DeleteAddressService {

    private final AddressRepository addressRepository;

    public DeleteAddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @Transactional
    public Address softDeleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByUserIdAndAddressId(userId, addressId)
                .orElseThrow(() -> new DataNotFoundException("Address not found!"));

        if (address.getDeletedAt() != null) {
            throw new RuntimeException("Address is already deleted!");
        }

        if (address.getIsMain()) {
            throw new RuntimeException("Main Address cannot be deleted!");
        }

        address.setDeletedAt(OffsetDateTime.now());
        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public void hardDeleteAllDeletedAddresses(Long userId) {
        List<Address> allDeletedAddress = addressRepository.findAllDeletedAddressesByUserId(userId);
        if (allDeletedAddress.isEmpty()){
            throw new DataNotFoundException("Deleted Address not found !");
        }

        addressRepository.deleteAll(allDeletedAddress);
    }

    @Override
    @Transactional
    public void hardDeleteDeletedAddress(Long userId, Long addressId) {
        Address deletedAddress = addressRepository.findDeletedAddressByUserIdAndAddressId(userId, addressId)
                .orElseThrow(() -> new DataNotFoundException("Address not found!"));

        addressRepository.delete(deletedAddress);
    }
}
