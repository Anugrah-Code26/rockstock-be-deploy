package com.rockstock.backend.infrastructure.productCategory.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCategoryRequestDTO {
    private Long categoryId;

    @Size(min = 3, max = 30, message = "Category Name must be between 3 and 30 characters long")
    private String categoryName;

    private MultipartFile file;
}