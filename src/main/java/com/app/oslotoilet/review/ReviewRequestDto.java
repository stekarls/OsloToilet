package com.app.oslotoilet.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {
    @NotNull(message = "Toilet ID is required")
    private UUID toiletId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Cleanliness rating is required")
    @Min(value = 0) @Max(value = 5)
    private Short cleanliness;

    @NotNull(message = "Equipment rating is required")
    @Min(value = 0) @Max(value = 5)
    private Short equipment;

    @NotNull(message = "Access rating is required")
    @Min(value = 0) @Max(value = 5)
    private Short access;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;
}
