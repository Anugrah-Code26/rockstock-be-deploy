package com.rockstock.backend.infrastructure.productPicture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePicturePositionRequestDTO {
    private Long pictureId;
    private Long productId;
    private int newPosition;
}
