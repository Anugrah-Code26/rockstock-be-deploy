package com.rockstock.backend.infrastructure.productCategory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCategoryResponseDTO {
    private Long categoryId;
    private String categoryName;
    private String categoryPicture;
}