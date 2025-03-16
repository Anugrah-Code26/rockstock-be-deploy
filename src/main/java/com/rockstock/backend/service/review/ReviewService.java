package com.rockstock.backend.service.review;

import com.rockstock.backend.infrastructure.review.dto.ReviewRequestDTO;
import com.rockstock.backend.infrastructure.review.dto.ReviewResponseDTO;
import com.rockstock.backend.infrastructure.review.dto.ReviewSummaryDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO addReview(ReviewRequestDTO request);
    List<ReviewSummaryDTO> getReviewSummaryByProduct(Long productId);
    List<ReviewResponseDTO> getAllReviews();

}

