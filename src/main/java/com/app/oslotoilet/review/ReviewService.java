package com.app.oslotoilet.review;

import com.app.oslotoilet.enums.ContributionPoints;
import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.security.SecurityUser;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ToiletRepository toiletRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public ReviewService(ToiletRepository toiletRepository, UserRepository userRepository, ReviewRepository reviewRepository){
        this.toiletRepository = toiletRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;

    }


    public List<ReviewResponseDto> getReviews(){
        return reviewRepository.findAll().stream().map(this::mapToResponseDto).toList();
    }

    public List<ReviewResponseDto> getReviewsByToiletId(UUID toiletId) {
        return reviewRepository.findByToiletId(toiletId).stream().map(this::mapToResponseDto).toList();
    }


    public List<ReviewResponseDto> getReviewsByUserId(UUID userId) {
        return reviewRepository.findByUserId(userId).stream().map(this::mapToResponseDto).toList();
    }



    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto){

        Toilet toilet = toiletRepository.findById(reviewRequestDto.getToiletId()).orElseThrow(() -> new EntityNotFoundException("Toilet not found with id: " + reviewRequestDto.getToiletId()));
        User user = userRepository.findById(reviewRequestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + reviewRequestDto.getUserId()));


        Review review = mapToEntity(reviewRequestDto, user, toilet);
        review = reviewRepository.save(review);
        user.setContributionPoints(user.getContributionPoints() + ContributionPoints.REVIEW.getValue());

        return mapToResponseDto(review);
    }

    public void deleteReview(UUID reviewId, SecurityUser currentUser) {
        boolean isAdmin = currentUser.getUser().getRole() == Role.ADMIN;

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("Review not found with reviewId: " + reviewId));

        if (!isAdmin && !review.getUser().getId().equals(currentUser.getUser().getId())){
            throw new AccessDeniedException("You can only delete your own reviews");
        }
        reviewRepository.deleteById(reviewId);
    }



    private Review mapToEntity(ReviewRequestDto reviewRequestDto, User user, Toilet toilet){
        double averageRating = getAverageRating(reviewRequestDto.getCleanliness(), reviewRequestDto.getAccess(), reviewRequestDto.getEquipment());
        return Review.builder()
                .user(user)
                .toilet(toilet)
                .cleanliness(reviewRequestDto.getCleanliness())
                .access(reviewRequestDto.getAccess())
                .equipment(reviewRequestDto.getEquipment())
                .comment(reviewRequestDto.getComment())
                .created(OffsetDateTime.now())
                .averageRating(averageRating)
                .build();
    }

    private ReviewResponseDto mapToResponseDto(Review review){
        return ReviewResponseDto.builder()
                .id(review.getId())
                .toiletId(review.getToilet().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getNickname())
                .cleanliness(review.getCleanliness())
                .access(review.getAccess())
                .equipment(review.getEquipment())
                .averageRating(review.getAverageRating())
                .comment(review.getComment())
                .created(review.getCreated())
                .build();
    }

    public double getAverageRating(Short cleanliness, Short equipment, Short access){
        return (cleanliness + equipment + access) / 3.0;
    }
}
