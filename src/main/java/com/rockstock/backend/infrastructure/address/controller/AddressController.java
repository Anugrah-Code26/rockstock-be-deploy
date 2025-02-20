package com.rockstock.backend.infrastructure.address.controller;

import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.infrastructure.address.dto.CreateAddressRequestDTO;
import com.rockstock.backend.infrastructure.address.dto.UpdateAddressRequestDTO;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.service.address.CreateAddressService;
import com.rockstock.backend.service.address.DeleteAddressService;
import com.rockstock.backend.service.address.GetAddressService;
import com.rockstock.backend.service.address.UpdateAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final CreateAddressService createAddressService;
    private final GetAddressService getAddressService;
    private final UpdateAddressService updateAddressService;
    private final DeleteAddressService deleteAddressService;

    // Create
    @PostMapping
    public ResponseEntity<?> createAddress(@Valid @RequestBody CreateAddressRequestDTO req) {
        return ApiResponse.success(HttpStatus.OK.value(), "Create address success!", createAddressService.createAddress(req));
    }

    // Read / Get
    @GetMapping
    public ResponseEntity<?> getAddressesByUserId() {
        return ApiResponse.success(HttpStatus.OK.value(), "Get all user addresses success!", getAddressService.getAddressesByUserId(Claims.getUserIdFromJwt()));
    }

    @GetMapping("/provinces")
    public ResponseEntity<?> getAddressesByUserIdAndProvinceId(@RequestParam Long provinceId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get addresses by province id success!", getAddressService.getAddressesByUserIdAndProvinceId(Claims.getUserIdFromJwt(), provinceId));
    }

    @GetMapping("/provinces/province")
    public ResponseEntity<?> getAddressesByUserIdAndProvinceName(@RequestParam String name) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get addresses by province name success!", getAddressService.getAddressesByUserIdAndProvinceName(Claims.getUserIdFromJwt(), name));
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getAddressesByUserIdAndCityId(@RequestParam Long cityId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get addresses by city id success!", getAddressService.getAddressesByUserIdAndCityId(Claims.getUserIdFromJwt(), cityId));
    }

    @GetMapping("/cities/city")
    public ResponseEntity<?> getAddressesByUserIdAndCityName(@RequestParam String name) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get addresses by city name success!", getAddressService.getAddressesByUserIdAndCityName(Claims.getUserIdFromJwt(), name));
    }

    @GetMapping("/districts")
    public ResponseEntity<?> getAddressesByUserIdAndDistrictId(@RequestParam Long districtId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get addresses by district id success!", getAddressService.getAddressesByUserIdAndDistrictId(Claims.getUserIdFromJwt(), districtId));
    }

    @GetMapping("/districts/district")
    public ResponseEntity<?> getAddressesByUserIdAndDistrictName(@RequestParam String name) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get addresses by district name success!", getAddressService.getAddressesByUserIdAndDistrictName(Claims.getUserIdFromJwt(), name));
    }

    @GetMapping("/sub-districts")
    public ResponseEntity<?> getAddressesByUserIdAndSubDistrictId(@RequestParam Long subDistrictId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get addresses by sub-district id success!", getAddressService.getAddressesByUserIdAndSubDistrictId(Claims.getUserIdFromJwt(), subDistrictId));
    }

    @GetMapping("/sub-districts/sub-district")
    public ResponseEntity<?> getAddressesByUserIdAndSubDistrictName(@RequestParam String name) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get addresses by sub-district name success!", getAddressService.getAddressesByUserIdAndSubDistrictName(Claims.getUserIdFromJwt(), name));
    }

    @GetMapping("/user-address")
    public ResponseEntity<?> getAddressByUserIdAndAddressId(@RequestParam Long addressId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get user address success!", getAddressService.getAddressByUserIdAndAddressId(Claims.getUserIdFromJwt(), addressId));
    }

    @GetMapping("/label")
    public ResponseEntity<?> getAddressByUserIdAndLabel(@RequestParam String label) {
        return ApiResponse.success(HttpStatus.OK.value(), "Get user address by label success!", getAddressService.getAddressByUserIdAndLabel(Claims.getUserIdFromJwt(), label));
    }

    @GetMapping("/main")
    public ResponseEntity<?> getMainAddressByUserId() {
        return ApiResponse.success(HttpStatus.OK.value(), "Get user main address success!", getAddressService.getMainAddressByUserId(Claims.getUserIdFromJwt()));
    }

    @GetMapping("/trash")
    public ResponseEntity<?> getAllDeletedAddressesByUserId() {
        return ApiResponse.success(HttpStatus.OK.value(), "Get all deleted addresses successfully!", getAddressService.getAllDeletedAddressesByUserId(Claims.getUserIdFromJwt()));
    }

    // Update
    @PutMapping("/update")
    public ResponseEntity<?> updateAddress(@RequestParam Long addressId, @RequestBody UpdateAddressRequestDTO req) {
        return ApiResponse.success(HttpStatus.OK.value(), "Update address success!", updateAddressService.updateAddress(Claims.getUserIdFromJwt(), addressId, req));
    }

    @PutMapping("/change-main")
    public ResponseEntity<?> updateMainAddress(@RequestParam Long addressId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Update address success!", updateAddressService.updateMainAddress(Claims.getUserIdFromJwt(), addressId));
    }

    @PutMapping("/restore")
    public ResponseEntity<?> restoreDeletedAddress(@RequestParam Long addressId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Address restored successfully!", updateAddressService.restoreDeletedAddress(Claims.getUserIdFromJwt(), addressId));
    }

    @PutMapping("/restore-all")
    public ResponseEntity<?> restoreAllDeletedAddresses() {
        return ApiResponse.success(HttpStatus.OK.value(), "All deleted addresses restored successfully!", updateAddressService.restoreAllDeletedAddresses(Claims.getUserIdFromJwt()));
    }

    // Delete
    @PutMapping("/soft-delete")
    public ResponseEntity<?> softDeleteAddress(@RequestParam Long addressId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Address moved to trash!", deleteAddressService.softDeleteAddress(Claims.getUserIdFromJwt(), addressId));
    }

    @DeleteMapping("/hard-delete")
    public ResponseEntity<?> hardDeleteDeletedAddress(@RequestParam Long addressId) {
        deleteAddressService.hardDeleteDeletedAddress(Claims.getUserIdFromJwt(), addressId);
        return ApiResponse.success("Address permanently deleted!");
    }

    @DeleteMapping("/hard-delete/clear-all")
    public ResponseEntity<?> hardDeleteAllDeletedAddresses() {
        deleteAddressService.hardDeleteAllDeletedAddresses(Claims.getUserIdFromJwt());
        return ApiResponse.success("All address in trash permanently deleted!");
    }

}
