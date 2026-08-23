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

    @Min(value = 1, message = "Cleanliness rating must be at least 1")
    @Max(value = 5, message = "Cleanliness rating cannot exceed 5")
    @NotNull(message = "Cleanliness rating is required")
    private Short cleanliness;

    @Min(value = 1, message = "Equipment rating must be at least 1")
    @Max(value = 5, message = "Equipment rating cannot exceed 5")
    @NotNull(message = "Equipment rating is required")
    private Short equipment;

    @Min(value = 1, message = "Access rating must be at least 1")
    @Max(value = 5, message = "Access rating cannot exceed 5")
    @NotNull(message = "Access rating is required")
    private Short access;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;
}
