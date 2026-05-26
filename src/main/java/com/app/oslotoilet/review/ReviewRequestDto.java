package com.app.oslotoilet.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {
    @NotNull(message = "Toilet ID is required")
    private UUID toiletId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Cleanliness rating is required")
    @Min(0) @Max(5)
    private Byte cleanliness;

    @NotNull(message = "Equipment rating is required")
    @Min(0) @Max(5)
    private Byte equipment;

    @NotNull(message = "Access rating is required")
    @Min(0) @Max(5)
    private Byte access;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;
}
