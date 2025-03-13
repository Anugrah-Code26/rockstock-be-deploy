package com.rockstock.backend.infrastructure.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDTO {
    @NotBlank
    @Size(min = 3, max = 30, message = "Product name must be between 3 and 30 characters long")
    private String productName;

    @NotBlank
    @Size(min = 3, max = 100, message = "Details must be between 3 and 100 characters long")
    private String detail;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigDecimal weight;

    @NotNull
    private Long categoryId;
}