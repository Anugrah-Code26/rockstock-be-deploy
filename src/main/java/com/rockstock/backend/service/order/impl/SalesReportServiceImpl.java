package com.rockstock.backend.service.order.impl;

import com.rockstock.backend.entity.order.OrderStatusList;
import com.rockstock.backend.infrastructure.order.dto.SalesReportDTO;
import com.rockstock.backend.infrastructure.order.repository.SalesReportRepository;
import com.rockstock.backend.infrastructure.user.auth.security.Claims;
import com.rockstock.backend.service.order.SalesReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesReportServiceImpl implements SalesReportService {
    private final SalesReportRepository salesReportRepository;

    @Override
    public List<SalesReportDTO> getSalesReportByMonth(int year, Long warehouseId, Long productId, Long productCategoryId) {
        String role = Claims.getRoleFromJwt();
        List<Long> warehouseIds = Claims.getWarehouseIdsFromJwt();

        System.out.println("Report Role: ");
        System.out.println(role);

        if ("Customer".equals(role)) {
            throw new AuthorizationDeniedException("You do not have access!");
        }

        if ("Warehouse Admin".equals(role) && !warehouseIds.contains(warehouseId)) {
            throw new AuthorizationDeniedException("You do not have access to this warehouse!");
        }

        return salesReportRepository.findSalesReport(
                OrderStatusList.COMPLETED,
                year,
                warehouseId,
                productId,
                productCategoryId
        );
    }
}