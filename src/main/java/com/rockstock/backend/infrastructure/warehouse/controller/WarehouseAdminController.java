package com.rockstock.backend.infrastructure.warehouse.controller;

import com.rockstock.backend.infrastructure.warehouse.dto.AssignWarehouseAdminDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseAdminResponseDTO;
import com.rockstock.backend.service.warehouse.WarehouseAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse-admins")
@RequiredArgsConstructor
public class WarehouseAdminController {

    private final WarehouseAdminService warehouseAdminService;

    @PostMapping("/assign")
    public ResponseEntity<Void> assignWarehouseAdmin(@RequestBody AssignWarehouseAdminDTO requestDTO) {
        String currentUserRole = "Super Admin";
        warehouseAdminService.assignWarehouseAdmin(requestDTO, currentUserRole);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<WarehouseAdminResponseDTO>> getAllWarehouseAdmins() {
        List<WarehouseAdminResponseDTO> admins = warehouseAdminService.getAllWarehouseAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<List<WarehouseAdminResponseDTO>> getWarehouseAdmins(@PathVariable Long warehouseId) {
        List<WarehouseAdminResponseDTO> admins = warehouseAdminService.getWarehouseAdmins(warehouseId);
        return ResponseEntity.ok(admins);
    }

    @DeleteMapping("/remove/{warehouseAdminId}")
    @PreAuthorize("hasRole('Super Admin')")
    public ResponseEntity<Void> removeWarehouseAdmin(@PathVariable Long warehouseAdminId) {
        warehouseAdminService.removeWarehouseAdmin(warehouseAdminId);
        return ResponseEntity.noContent().build();
    }
}
