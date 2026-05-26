package com.app.oslotoilet.toilet;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToiletRequestDto {

    @NotBlank
    @Size(min = 5, message = "Name must be 5 characters in length or more")
    @Size(max = 128, message = "Description cannot exceed 128 characters")
    private String name;

    @NotNull(message = "Latitude is required")
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal longitude;

    private boolean hasFee;

    @NotNull(message = "Fee amount is required")
    @DecimalMin("0.0")
    private BigDecimal fee;

    @NotBlank
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private boolean hasConditions;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String conditions;

    private boolean isSeasonal;
    private boolean isClosed;



}
