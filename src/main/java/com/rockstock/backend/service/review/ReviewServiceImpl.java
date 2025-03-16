package com.rockstock.backend.service.review;

import com.rockstock.backend.entity.product.Product;
import com.rockstock.backend.entity.review.Review;
import com.rockstock.backend.entity.user.User;
import com.rockstock.backend.entity.user.UserRole;
import com.rockstock.backend.infrastructure.product.repository.ProductRepository;
import com.rockstock.backend.infrastructure.review.dto.ReviewRequestDTO;
import com.rockstock.backend.infrastructure.review.dto.ReviewResponseDTO;
import com.rockstock.backend.infrastructure.review.dto.ReviewSummaryDTO;
import com.rockstock.backend.infrastructure.review.repository.ReviewRepository;
import com.rockstock.backend.infrastructure.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ReviewResponseDTO addReview(ReviewRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setReview(request.getReview());

        Review savedReview = reviewRepository.save(review);

        return new ReviewResponseDTO(
                savedReview.getId(),                                    // reviewId
                savedReview.getProduct().getProductName(),              // productName
                savedReview.getUser().getFullname(),                    // fullname
                savedReview.getUser().getPhotoProfileUrl(),             // photoProfileUrl
                savedReview.getUser().getId(),                          // userId
                savedReview.getRating(),                                // rating
                savedReview.getReview()                                 // review
        );
    }

    @Override
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll().stream().map(review -> {
            User user = review.getUser();
            Product product = review.getProduct();

            return new ReviewResponseDTO(
                    review.getId(),
                    product.getProductName(),
                    user.getFullname(),
                    user.getPhotoProfileUrl(),
                    user.getId(),
                    review.getRating(),
                    review.getReview()
            );
        }).collect(Collectors.toList());
    }


    @Override
    public List<ReviewSummaryDTO> getReviewSummaryByProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);

        return reviews.stream().map(review -> {
            User user = review.getUser();
            Product product = review.getProduct();

            // Ambil role dari UserRole (User <-> UserRole <-> Role)
            UserRole userRole = user.getUserRoles().stream().findFirst().orElse(null);
            String roleName = (userRole != null) ? userRole.getRole().getName() : "User";

            return new ReviewSummaryDTO(
                    product.getProductName(),
                    user.getId(),
                    user.getPhotoProfileUrl(),
                    user.getFullname(),
                    review.getRating(),
                    review.getReview(),
                    roleName
            );
        }).collect(Collectors.toList());
    }
}
