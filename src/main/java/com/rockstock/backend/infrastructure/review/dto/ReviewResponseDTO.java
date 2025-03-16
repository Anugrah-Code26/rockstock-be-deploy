package com.rockstock.backend.infrastructure.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponseDTO {
    private Long reviewId;
    private String productName;
    private String fullname;
    private String photoProfileUrl;
    private Long userId;
    private Integer rating;
    private String review;
}
