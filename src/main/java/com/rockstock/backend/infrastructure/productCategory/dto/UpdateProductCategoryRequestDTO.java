package com.rockstock.backend.infrastructure.productCategory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCategoryRequestDTO {
    private Long categoryId;
    private String categoryName; // Optional
    private MultipartFile file;  // Optional
}