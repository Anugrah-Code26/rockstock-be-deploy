package com.rockstock.backend.infrastructure.mutationJournal.controller;

import com.rockstock.backend.entity.stock.MutationJournal;
import com.rockstock.backend.infrastructure.mutationJournal.dto.StockSummaryResponse;
import com.rockstock.backend.service.mutation.StockReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports/stock")
@RequiredArgsConstructor
public class StockReportController {

    private final StockReportService stockReportService;

    @GetMapping("/summary")
    public ResponseEntity<List<StockSummaryResponse>> getMonthlySummary(
            @RequestParam Long warehouseId) {
        List<StockSummaryResponse> summary = stockReportService.getMonthlySummary(warehouseId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/product")
    public ResponseEntity<List<StockSummaryResponse>> getMonthlySummaryForProduct(
            @RequestParam Long warehouseId,
            @RequestParam Long productId) {
        List<StockSummaryResponse> productSummary = stockReportService.getMonthlySummaryForProduct(warehouseId, productId);
        return ResponseEntity.ok(productSummary);
    }

    @GetMapping("/details")
    public ResponseEntity<List<MutationJournal>> getProductMutationsByMonth(
            @RequestParam Long warehouseId,
            @RequestParam Long productId,
            @RequestParam int year,
            @RequestParam int month) {
        List<MutationJournal> mutations = stockReportService.getProductMutationsByMonth(warehouseId, productId, year, month);
        return ResponseEntity.ok(mutations);
    }
}