package com.rockstock.backend.service.order;

import com.rockstock.backend.infrastructure.order.dto.SalesReportDTO;

import java.util.List;

public interface SalesReportService {
    List<SalesReportDTO> getSalesReportByMonth(int year, Long warehouseId, Long productId, Long productCategoryId);
}
