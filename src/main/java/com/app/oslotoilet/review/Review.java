package com.app.oslotoilet.review;


import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"toilet_id", "user_id"})
})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating_cleanliness")
    @Min(value = 1, message = "Cleanliness rating must be at least 1")
    @Max(value = 5, message = "Cleanliness rating cannot exceed 5")
    @NotNull(message = "Cleanliness rating is required")
    private Short cleanliness;

    @Min(value = 1, message = "Accessibility rating must be at least 1")
    @Max(value = 5, message = "Accessibility rating cannot exceed 5")
    @NotNull(message = "Equipment rating is required")
    @Column(name = "rating_equipment")
    private Short equipment;

    @Min(value = 1, message = "Accessibility rating must be at least 1")
    @Max(value = 5, message = "Accessibility rating cannot exceed 5")
    @NotNull(message = "Access rating is required")
    @Column(name = "rating_access")
    private Short access;

    @Column(name = "average_rating")
    private double averageRating;


    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime created;

}
