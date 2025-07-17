package com.rockstock.backend.infrastructure.mutationJournal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockSummaryResponse {
    private Long productId;
    private String productName;
    private int year;
    private int month;
    private Long totalAddition;
    private Long totalReduction;
    private Long endingStock;
}
