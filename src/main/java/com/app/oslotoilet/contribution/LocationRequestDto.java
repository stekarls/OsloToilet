package com.app.oslotoilet.contribution;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequestDto {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Latitude is required")
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal longitude;

    @NotBlank(message = "Description is required")
    private String description;

    private boolean hasFee;

    @NotNull(message = "Fee amount is required")
    @DecimalMin("0.0")
    private BigDecimal fee;
}