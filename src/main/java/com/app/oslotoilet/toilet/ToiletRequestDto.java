package com.app.oslotoilet.toilet;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToiletRequestDto {

    @NotBlank
    @Size(min = 5, max = 64, message = "Toilet name must be between 5 and 64 characters")
    private String name;

    @NotNull(message = "Latitude is required")
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal longitude;

    @NotNull(message = "hasFee is required")
    private Boolean hasFee;

    @DecimalMin("0.0")
    private BigDecimal fee;

    @NotBlank
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "alwaysOpen is required")
    private Boolean alwaysOpen;

    @Size(max = 1000, message = "Conditions cannot exceed 1000 characters")
    private String conditions;

    @NotNull(message = "seasonal is required")
    private Boolean seasonal;

    @NotNull(message = "closed is required")
    private Boolean closed;

    @SuppressWarnings("unused")
    @AssertTrue(message = "A fee amount greater than 0 is required when hasFee is true, and no fee when it is false")
    private boolean isFeeConsistent() {
        if (hasFee == null) {
            return true;
        }
        return hasFee
                ? fee != null && fee.compareTo(BigDecimal.ZERO) > 0
                : fee == null;
    }

}
