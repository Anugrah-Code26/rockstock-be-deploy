package com.rockstock.backend.service.warehouse;

import com.rockstock.backend.entity.warehouse.WarehouseAdmin;
import com.rockstock.backend.infrastructure.warehouse.dto.AssignWarehouseAdminDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseAdminResponseDTO;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseAdminRepository;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseAdminServiceImpl implements WarehouseAdminService {

    private final WarehouseAdminRepository warehouseAdminRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void assignWarehouseAdmin(AssignWarehouseAdminDTO request) {
        var warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var warehouseAdmin = new WarehouseAdmin();
        warehouseAdmin.setWarehouse(warehouse);
        warehouseAdmin.setUser(user);

        warehouseAdminRepository.save(warehouseAdmin);
    }

    @Override
    public List<WarehouseAdminResponseDTO> getAllWarehouseAdmins() {
        List<WarehouseAdmin> warehouseAdmins = warehouseAdminRepository.findAll();
        return warehouseAdmins.stream()
                .map(admin -> new WarehouseAdminResponseDTO(
                        admin.getId(),
                        admin.getUser().getId(),
                        admin.getUser().getFullname(),
                        admin.getUser().getEmail(),
                        admin.getWarehouse().getId(),   // Tambahkan warehouseId
                        admin.getWarehouse().getName() // Tambahkan warehouseName
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    @Override
    public void removeWarehouseAdmin(Long warehouseAdminId) {
        var warehouseAdmin = warehouseAdminRepository.findById(warehouseAdminId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse Admin not found"));

        warehouseAdminRepository.delete(warehouseAdmin);
    }

    @Override
    public List<WarehouseAdminResponseDTO> getWarehouseAdmins(Long warehouseId) {
        return warehouseAdminRepository.findByWarehouseId(warehouseId).stream()
                .map(admin -> new WarehouseAdminResponseDTO(
                        admin.getId(),
                        admin.getUser().getId(),
                        admin.getUser().getFullname(),
                        admin.getUser().getEmail(),
                        admin.getWarehouse().getId(),
                        admin.getWarehouse().getName()
                ))
                .collect(Collectors.toList());
    }
}
