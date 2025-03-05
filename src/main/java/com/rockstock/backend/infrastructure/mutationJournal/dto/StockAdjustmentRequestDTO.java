package com.rockstock.backend.infrastructure.mutationJournal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequestDTO {
    @NotNull
    private Long newStockQuantity;

    @NotBlank
    private String description;
}