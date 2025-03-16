package com.rockstock.backend.infrastructure.admin.controller;

import com.rockstock.backend.infrastructure.admin.dto.AdminCreateRequestDTO;
import com.rockstock.backend.infrastructure.admin.dto.AdminResponseDTO;
import com.rockstock.backend.infrastructure.admin.dto.AdminUpdateRequestDTO;
import com.rockstock.backend.service.admin.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(
            @RequestBody AdminCreateRequestDTO request) {

        return ResponseEntity.ok(adminService.createAdmin(request));
    }



    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }


    @PutMapping("/{adminId}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable Long adminId, @RequestBody AdminUpdateRequestDTO request) {
        AdminResponseDTO updatedAdmin = adminService.updateAdmin(adminId, request);
        return ResponseEntity.ok(adminService.updateAdmin(adminId, request));
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long adminId, @RequestHeader("X-Requester-Id") Long requesterId) {
        adminService.deleteAdmin(requesterId, adminId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/soft-delete/{adminId}")
    public ResponseEntity<Void> softDeleteAdmin(@PathVariable Long adminId) {
        try {
            adminService.softDeleteAdmin(adminId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 500 Internal Server Error
        }
    }
}
