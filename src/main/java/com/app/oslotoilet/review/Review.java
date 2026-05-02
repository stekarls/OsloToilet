package com.app.oslotoilet.review;


import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reviews")
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
    private Integer cleanliness;

    @Column(name = "rating_equipment")
    private Integer equipment;

    @Column(name = "rating_access")
    private Integer access;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime created = OffsetDateTime.now();


    public double getAverageRating(){
        return (cleanliness + equipment + access) / 3.0;
    }

}
