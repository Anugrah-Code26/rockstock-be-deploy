package com.rockstock.backend.infrastructure.product.dto;

import com.rockstock.backend.entity.product.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateProductResponseDTO {
    private Long productId;
    private ProductStatus status;
}