package com.rockstock.backend.infrastructure.productPicture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePicturePositionResponseDTO {
    private Long pictureId;
    private String productPictureUrl;
    private int position;
}
