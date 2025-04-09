package com.rockstock.backend.service.warehouse.impl;

import com.rockstock.backend.common.utils.DistanceCalculator;
import com.rockstock.backend.entity.warehouse.Warehouse;
import com.rockstock.backend.infrastructure.warehouse.repository.WarehouseRepository;
import com.rockstock.backend.service.warehouse.FindNearestWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindNearestWarehouseServiceImpl implements FindNearestWarehouseService {
    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public Warehouse findNearestWarehouse(String latitude, String longitude) {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        Warehouse nearestWarehouse = null;
        double minDistance = Double.MAX_VALUE;

        double lat1 = Double.parseDouble(latitude);
        double lng1 = Double.parseDouble(longitude);

        for (Warehouse warehouse : warehouses) {
            double lat2 = Double.parseDouble(warehouse.getLatitude());
            double lng2 = Double.parseDouble(warehouse.getLongitude());
            double distance = DistanceCalculator.haversine(lat1, lng1, lat2, lng2);

            if (distance < minDistance) {
                minDistance = distance;
                nearestWarehouse = warehouse;
            }
        }

        if (nearestWarehouse == null) {
            throw new RuntimeException("No warehouse found");
        }
        return nearestWarehouse;
    }
}
