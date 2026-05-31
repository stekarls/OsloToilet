package com.app.oslotoilet.review;


import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    UUID id;
    UUID toiletId;
    UUID userId;
    String userName;
    Short cleanliness;
    Short equipment;
    Short access;
    double averageRating;
    String comment;
    OffsetDateTime created;
}
