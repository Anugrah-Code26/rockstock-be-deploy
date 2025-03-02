package com.rockstock.backend.infrastructure.warehouse.controller;

import com.rockstock.backend.infrastructure.warehouse.dto.AssignWarehouseAdminDTO;
import com.rockstock.backend.service.warehouse.WarehouseAdminService;  // Use WarehouseAdminService
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
