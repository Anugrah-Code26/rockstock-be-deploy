package com.rockstock.backend.infrastructure.review.dto;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Long userId;
    private Long productId;
    private Integer rating;
    private String review;
}
