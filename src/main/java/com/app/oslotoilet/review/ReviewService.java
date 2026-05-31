package com.app.oslotoilet.review;

import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

        Toilet toilet = toiletRepository.findById(reviewRequestDto.getToiletId()).orElseThrow(() -> new EntityNotFoundException("Toilet not found"));
        User user = userRepository.findById(reviewRequestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found"));


        Review review = mapToEntity(reviewRequestDto, user, toilet);
        review = reviewRepository.save(review);

        return mapToResponseDto(review);
    }

    public void deleteReview(UUID id){
        if (!reviewRepository.existsById(id)){
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(id);
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
