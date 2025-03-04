package com.rockstock.backend.infrastructure.address.controller;

import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.infrastructure.address.dto.CreateAddressRequestDTO;
import com.rockstock.backend.infrastructure.address.dto.GetAddressResponseDTO;
import com.rockstock.backend.infrastructure.address.dto.UpdateAddressRequestDTO;
import com.rockstock.backend.service.address.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final CreateAddressService createAddressService;
    private final GetAddressService getAddressService;
    private final UpdateAddressService updateAddressService;
    private final DeleteAddressService deleteAddressService;
    private final SetMainAddressService setMainAddressService;

    public AddressController(
            CreateAddressService createAddressService,
            GetAddressService getAddressService,
            UpdateAddressService updateAddressService,
            DeleteAddressService deleteAddressService,
            SetMainAddressService setMainAddressService
    ) {
        this.createAddressService = createAddressService;
        this.getAddressService = getAddressService;
        this.updateAddressService = updateAddressService;
        this.deleteAddressService = deleteAddressService;
        this.setMainAddressService = setMainAddressService;
    }

    // Create Address
    @PostMapping
    public ResponseEntity<?> createAddress(@Valid @RequestBody CreateAddressRequestDTO req) {
        return ApiResponse.success(HttpStatus.OK.value(), "Create address success!", createAddressService.createAddress(req));
    }

    // Set Main Address (Updated)
    @PatchMapping("/{addressId}/user/{userId}/set-main")
    public ResponseEntity<?> setMainAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId
    ) {
        setMainAddressService.setMainAddress(userId, addressId);
        return ApiResponse.success("Set main address success!");
    }

    // Get All User Addresses
    @GetMapping("/users")
    public ResponseEntity<?> getAddressesByUserId(@RequestParam Long userId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get all user addresses success!",
                getAddressService.getAddressesByUserId(userId));
    }

    // Get Address by User ID and Address ID
    @GetMapping("/{addressId}/user/{userId}")
    public ResponseEntity<?> getAddressByUserIdAndAddressId(@PathVariable Long userId, @PathVariable Long addressId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get user address success!",
                getAddressService.getAddressByUserIdAndAddressId(userId, addressId));
    }

    // Get Main Address by User ID (Updated)
    @GetMapping("/user/{userId}/main")
    public ResponseEntity<?> getMainAddressByUserId(@PathVariable Long userId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get main address success!",
                getAddressService.getMainAddressByUserId(userId, true));
    }

    // Update Address
    @PutMapping("/{addressId}/user/{userId}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @RequestBody UpdateAddressRequestDTO req
    ) {
        return ApiResponse.success(HttpStatus.OK.value(), "Update address success!",
                updateAddressService.updateAddress(userId, addressId, req));
    }

    // Soft Delete Address
    @PutMapping("/soft-delete/{addressId}/user/{userId}")
    public ResponseEntity<?> softDeleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Address moved to trash!",
                deleteAddressService.softDeleteAddress(userId, addressId));
    }

    // Hard Delete Single Address
    @DeleteMapping("/hard-delete/{addressId}/user/{userId}")
    public ResponseEntity<?> hardDeleteDeletedAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        deleteAddressService.hardDeleteDeletedAddress(userId, addressId);
        return ApiResponse.success("Address permanently deleted!");
    }

    // Hard Delete All Deleted Addresses
    @DeleteMapping("/hard-delete/clear-all/user/{userId}")
    public ResponseEntity<?> hardDeleteAllDeletedAddresses(@PathVariable Long userId) {
        deleteAddressService.hardDeleteAllDeletedAddresses(userId);
        return ApiResponse.success("All addresses in trash permanently deleted!");
    }
}
