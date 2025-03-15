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
    public List<SalesReportDTO> getSalesReport(int month, int year, Long warehouseId, Long productId, Long productCategoryId) {
        String role = Claims.getRoleFromJwt();
        List<Long> warehouseIds = Claims.getWarehouseIdsFromJwt();

        if ("Customer".equals(role)) {
            throw new AuthorizationDeniedException("You do not have access!");
        }

        if ("Warehouse Admin".equals(role) && !warehouseIds.contains(warehouseId)) {
            throw new AuthorizationDeniedException("You do not have access to this warehouse!");
        }

        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
        OffsetDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0, 0).atOffset(zoneOffset);
        OffsetDateTime endDateTime = startDateTime.plusMonths(1).minusSeconds(1);

        return salesReportRepository.findSalesReport(OrderStatusList.COMPLETED, startDateTime, endDateTime, warehouseId, productId, productCategoryId);
    }
}