package com.rockstock.backend.infrastructure.productCategory.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetListProductCategoryResponseDTO {
    private Long categoryId;
    private String categoryName;
}