package com.rockstock.backend.infrastructure.productPicture.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductPictureRequestDTO {
    private Long productId;
    @NotNull
    private String imageUrl;
}