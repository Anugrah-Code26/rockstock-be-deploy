package com.rockstock.backend.infrastructure.mutationJournal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MutationResponseDTO {
    private Long stockId;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Long stockQuantity;
}