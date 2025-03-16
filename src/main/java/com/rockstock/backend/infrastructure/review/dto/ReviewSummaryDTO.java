package com.rockstock.backend.infrastructure.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewSummaryDTO {
    private String productName;
    private Long userId;
    private String photoProfileUrl;
    private String fullname;
    private Integer rating;
    private String review;
    private String role;
}
