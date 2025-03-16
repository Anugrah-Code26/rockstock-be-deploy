package com.rockstock.backend.infrastructure.review.controller;
import com.rockstock.backend.infrastructure.review.dto.ReviewRequestDTO;
import com.rockstock.backend.infrastructure.review.dto.ReviewResponseDTO;
import com.rockstock.backend.infrastructure.review.dto.ReviewSummaryDTO;
import com.rockstock.backend.service.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Tambah review baru
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> addReview(@Valid @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.ok(reviewService.addReview(request));
    }

    // Ambil semua review
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }


    // Ambil semua review berdasarkan productId
    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<List<ReviewSummaryDTO>> getReviewSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewSummaryByProduct(productId));
    }
}
