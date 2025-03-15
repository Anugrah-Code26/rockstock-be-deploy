package com.rockstock.backend.service.order;

import com.rockstock.backend.infrastructure.order.dto.SalesReportDTO;

import java.util.List;

public interface SalesReportService {
    List<SalesReportDTO> getSalesReport(int month, int year, Long warehouseId, Long productId, Long productCategoryId);
}
