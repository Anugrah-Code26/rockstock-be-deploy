package com.rockstock.backend.infrastructure.productCategory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCategoryRequestDTO {
    @NotBlank
    @Size(min = 3, max = 30, message = "Category name must be between 3 and 30 characters long")
    private String categoryName;

    @NotBlank
    private String categoryPicture;
}
