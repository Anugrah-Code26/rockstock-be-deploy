package com.rockstock.backend.service.warehouse;

import com.rockstock.backend.entity.geolocation.City;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.rockstock.backend.infrastructure.warehouse.dto.WarehouseResponseDTO;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setLongitude(request.getLongitude());
        warehouse.setLatitude(request.getLatitude());

        City city = new City();
        city.setId(request.getCityId());
        warehouse.setCity(city);

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return mapToDTO(savedWarehouse);
    }

    @Override
    public List<WarehouseResponseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseResponseDTO updateWarehouse(Long warehouseId, WarehouseRequestDTO request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setLongitude(request.getLongitude());
        warehouse.setLatitude(request.getLatitude());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);

        System.out.println("Updated Warehouse: " + updatedWarehouse); // Tambahkan log
        return mapToDTO(updatedWarehouse);
    }


    @Override
    public void deleteWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        warehouseRepository.delete(warehouse);
    }

    private WarehouseResponseDTO mapToDTO(Warehouse warehouse) {
        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setAddress(warehouse.getAddress());
        dto.setLongitude(warehouse.getLongitude());
        dto.setLatitude(warehouse.getLatitude());
//        dto.setCityName(warehouse.getCity().getName());
        return dto;
    }
}
