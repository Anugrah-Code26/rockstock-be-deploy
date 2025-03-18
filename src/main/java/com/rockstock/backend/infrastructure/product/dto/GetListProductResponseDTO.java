package com.rockstock.backend.infrastructure.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetListProductResponseDTO {
    private Long productId;
    private String productName;
    private String productCategory;
    private Long categoryId;
}