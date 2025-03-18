package com.rockstock.backend.infrastructure.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class SalesReportDTO {

    private Long warehouseId;
    private String warehouseName;
    private Long productId;
    private String productName;
    private Long productCategoryId;
    private String categoryName;
    private Integer totalQuantity;
    private BigDecimal totalSales;
    private int year;
    private int month;

    public SalesReportDTO(Long warehouseId, String warehouseName, Long productId, String productName,
                          Long productCategoryId, String categoryName, Integer totalQuantity, BigDecimal totalSales,
                          int year, int month) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.productId = productId;
        this.productName = productName;
        this.productCategoryId = productCategoryId;
        this.categoryName = categoryName;
        this.totalQuantity = totalQuantity;
        this.totalSales = totalSales;
        this.year = year;
        this.month = month;
    }

}
