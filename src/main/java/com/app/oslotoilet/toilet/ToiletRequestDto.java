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

    @DecimalMin("0.0")
    private BigDecimal fee;

    @NotBlank
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private boolean alwaysOpen;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String conditions;

    private boolean isSeasonal;

    private boolean isClosed;

    @SuppressWarnings("unused")
    @AssertTrue(message = "A fee amount greater than 0 is required when hasFee is true, and no fee when it is false")
    private boolean isFeeConsistent() {
        return hasFee
                ? fee != null && fee.compareTo(BigDecimal.ZERO) > 0
                : fee == null;
    }

}
