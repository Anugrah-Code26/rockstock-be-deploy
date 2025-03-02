package com.rockstock.backend.infrastructure.productPicture.dto;

import com.rockstock.backend.entity.product.ProductPicture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductPicturesResponseDTO {
    private Long pictureId;
    private String productPictureUrl;
    private int position;

    public static GetProductPicturesResponseDTO fromProductPicture(ProductPicture productPicture) {
        return new GetProductPicturesResponseDTO(
                productPicture.getId(),
                productPicture.getProductPictureUrl(),
                productPicture.getPosition()
        );
    }
}