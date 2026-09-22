package com.app.oslotoilet.review;


import com.app.oslotoilet.security.SecurityUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {


    private final ReviewService reviewService;


    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }


    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@RequestParam(required = false) UUID toiletId, @RequestParam(required = false) UUID userId){
        List<ReviewResponseDto> reviews = reviewService.getReviews(toiletId, userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/toilets/{toiletId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForToilet(@PathVariable UUID toiletId){
        List<ReviewResponseDto> reviews = reviewService.getReviewsForToilet(toiletId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewRequestDto request, @AuthenticationPrincipal SecurityUser currentUser) {
        ReviewResponseDto createdReview = reviewService.createReview(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId, @AuthenticationPrincipal SecurityUser currentUser) {
        reviewService.deleteReview(reviewId, currentUser);
        return ResponseEntity.noContent().build();
    }


}
