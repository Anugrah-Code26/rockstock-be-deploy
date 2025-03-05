package com.rockstock.backend.infrastructure.product.dto;

import com.rockstock.backend.entity.product.ProductStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class CreateProductResponseDTO {
    private Long productId;
    private ProductStatus status;
}