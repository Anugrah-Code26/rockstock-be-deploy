package com.rockstock.backend.infrastructure.productCategory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCategoryRequestDTO {
    @NotBlank
    private String categoryName;

    @NotBlank
    private String categoryPicture;
}
