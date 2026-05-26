package com.app.oslotoilet.review;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {


    private final ReviewService reviewService;


    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }


    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews(){
        List<ReviewResponseDto> reviews = reviewService.getReviews();
        return ResponseEntity.ok(reviews);
    }
    @GetMapping("/toilet/{toiletId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByToiletId(@PathVariable UUID toiletId){
        List<ReviewResponseDto> reviews = reviewService.getReviewsByToiletId(toiletId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByUserId(@PathVariable UUID userId){
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewDto request) {
        ReviewResponseDto createdReview = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id){
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();


    }


}
