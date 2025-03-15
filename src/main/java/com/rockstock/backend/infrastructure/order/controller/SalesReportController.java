package com.rockstock.backend.infrastructure.order.controller;

import com.rockstock.backend.infrastructure.order.dto.SalesReportDTO;
import com.rockstock.backend.service.order.SalesReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class SalesReportController {
    private final SalesReportService salesReportService;

    @GetMapping("/sales")
    public ResponseEntity<List<SalesReportDTO>> getSalesReport(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long productCategoryId) {

        List<SalesReportDTO> salesReport = salesReportService.getSalesReport(month, year, warehouseId, productId, productCategoryId);
        return ResponseEntity.ok(salesReport);
    }
}
