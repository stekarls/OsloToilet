package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.review.*;
import com.app.oslotoilet.security.SecurityUser;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTests {

    @Mock
    private ToiletRepository toiletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User author;
    private Toilet toilet;

    @BeforeEach
    void setUp() {
        author = User.builder().id(UUID.randomUUID()).nickname("author").role(Role.USER).contributionPoints(100L).build();
        toilet = Toilet.builder().id(UUID.randomUUID()).build();
    }

    private Review reviewBy(User user) {
        return Review.builder().id(UUID.randomUUID()).user(user).toilet(toilet).build();
    }

    @Nested
    class CreateReview {

        @BeforeEach
        void givenNewReview() {
            when(reviewRepository.existsByToiletIdAndUserId(toilet.getId(), author.getId())).thenReturn(false);
            when(toiletRepository.findById(toilet.getId())).thenReturn(Optional.of(toilet));
            when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
            when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        void createReview_shouldStoreTheAverageOfAllThreeRatings() {
            ReviewRequestDto request = new ReviewRequestDto(toilet.getId(), (short) 5, (short) 3, (short) 2, null);

            ReviewResponseDto response = reviewService.createReview(request, new SecurityUser(author));

            assertEquals(10 / 3.0, response.getAverageRating(), 1e-9);
        }

        @Test
        void createReview_shouldGiveTheAuthorPoints() {
            ReviewRequestDto request = new ReviewRequestDto(toilet.getId(), (short) 4, (short) 4, (short) 4, null);

            reviewService.createReview(request, new SecurityUser(author));

            assertEquals(120L, author.getContributionPoints());
        }
    }

    @Nested
    class DeleteReview {

        @Test
        void deleteReview_shouldTakeBackTheAuthorsPoints_whenAuthorDeletesIt() {
            Review review = reviewBy(author);
            when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

            reviewService.deleteReview(review.getId(), new SecurityUser(author));

            assertEquals(80L, author.getContributionPoints());
            verify(reviewRepository).delete(review);
        }

        @Test
        void deleteReview_shouldTakeBackTheAuthorsPoints_notTheAdmins_whenAdminDeletesIt() {
            User admin = User.builder().id(UUID.randomUUID()).role(Role.ADMIN).contributionPoints(500L).build();
            Review review = reviewBy(author);
            when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

            reviewService.deleteReview(review.getId(), new SecurityUser(admin));

            assertEquals(80L, author.getContributionPoints());
            assertEquals(500L, admin.getContributionPoints());
        }

        @Test
        void deleteReview_shouldNotLeaveNegativePoints() {
            author.setContributionPoints(5L);
            Review review = reviewBy(author);
            when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

            reviewService.deleteReview(review.getId(), new SecurityUser(author));

            assertEquals(0L, author.getContributionPoints());
        }

        @Test
        void deleteReview_shouldReject_andKeepPoints_whenUserDeletesSomeoneElsesReview() {
            User otherUser = User.builder().id(UUID.randomUUID()).role(Role.USER).contributionPoints(0L).build();
            Review review = reviewBy(author);
            when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

            assertThrows(AccessDeniedException.class, () -> reviewService.deleteReview(review.getId(), new SecurityUser(otherUser)));

            assertEquals(100L, author.getContributionPoints());
            verify(reviewRepository, never()).delete(any());
        }
    }
}
