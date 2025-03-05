package com.rockstock.backend.infrastructure.warehouse.controller;

import com.rockstock.backend.infrastructure.warehouse.dto.AssignWarehouseAdminDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseAdminResponseDTO;
import com.rockstock.backend.service.warehouse.WarehouseAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouse-admins")
@RequiredArgsConstructor
public class WarehouseAdminController {

    private final WarehouseAdminService warehouseAdminService;

    @PostMapping("/assign")
    public ResponseEntity<Void> assignWarehouseAdmin(@RequestBody AssignWarehouseAdminDTO requestDTO) {
        warehouseAdminService.assignWarehouseAdmin(requestDTO);
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
}