package com.rockstock.backend.infrastructure.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDTO {
    private Long warehouseId;
    private String warehouseName;
    private Long productId;
    private String productName;
    private Long productCategoryId;
    private String productCategoryName;
    private Integer quantity;
    private BigDecimal totalSales;
    private OffsetDateTime createdAt;
}

