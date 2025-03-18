package com.rockstock.backend.infrastructure.warehouse.controller;

import com.rockstock.backend.common.response.ApiResponse;
import com.rockstock.backend.infrastructure.warehouse.dto.AssignWarehouseAdminDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.UpdateWarehouseRequestDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseResponseDTO;
import com.rockstock.backend.service.warehouse.FindNearestWarehouseService;
import com.rockstock.backend.service.warehouse.WarehouseAdminService;
import com.rockstock.backend.service.warehouse.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final FindNearestWarehouseService findNearestWarehouseService;
    private final WarehouseService warehouseService;
    private final WarehouseAdminService warehouseAdminService;

    // CRUD Warehouse
    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> createWarehouse(@Valid @RequestBody WarehouseRequestDTO request) {
        return ResponseEntity.ok(warehouseService.createWarehouse(request));
    }

    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/warehouse-admin")
    public ResponseEntity<List<WarehouseResponseDTO>> getWarehousesByWarehouseAdmin() {
        return ResponseEntity.ok(warehouseService.getWarehousesByWarehouseAdmin());
    }

    @GetMapping("/nearest")
    public ResponseEntity<?> findNearestWarehouse(
            @RequestParam String latitude,
            @RequestParam String longitude
    ) {
        return ApiResponse.success(HttpStatus.OK.value(), "Nearest warehouse found!", findNearestWarehouseService.findNearestWarehouse(latitude, longitude));
    }

    @PutMapping("/{warehouseId}")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse(
            @PathVariable Long warehouseId,
            @Valid @RequestBody WarehouseRequestDTO request) {  // ← gunakan WarehouseRequestDTO
        return ResponseEntity.ok(warehouseService.updateWarehouse(warehouseId, request));
    }

    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long warehouseId) {
        warehouseService.deleteWarehouse(warehouseId);
        return ResponseEntity.noContent().build();
    }

//    // Assign & Remove Warehouse Admin
//    @PostMapping("/assign-admin")
//    public ResponseEntity<Void> assignWarehouseAdmin(@Valid @RequestBody AssignWarehouseAdminDTO request) {
//        warehouseAdminService.assignWarehouseAdmin(request);
//        return ResponseEntity.ok().build();
//    }

    @DeleteMapping("/remove-admin/{warehouseAdminId}")
    public ResponseEntity<Void> removeWarehouseAdmin(@PathVariable Long warehouseAdminId) {
        warehouseAdminService.removeWarehouseAdmin(warehouseAdminId);
        return ResponseEntity.noContent().build();
    }
}
