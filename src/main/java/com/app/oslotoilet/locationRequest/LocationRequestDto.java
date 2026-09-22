package com.app.oslotoilet.locationRequest;

import com.app.oslotoilet.enums.FeatureCode;
import com.app.oslotoilet.enums.PaymentCode;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 5, max = 64, message = "Toilet name must be between 5 and 64 characters")
    private String name;

    @NotNull(message = "Latitude is required")
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal longitude;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "hasFee is required")
    private Boolean hasFee;

    @DecimalMin(value = "0.0", inclusive = false, message = "Fee must be greater than 0")
    private BigDecimal fee;

    //Optional, the user may not know what the toilet offers
    private Set<@NotNull FeatureCode> featureCodes;

    private Set<@NotNull PaymentCode> paymentCodes;

    @SuppressWarnings("unused")
    @AssertTrue(message = "A fee amount greater than 0 is required when hasFee is true")
    private boolean isFeeConsistent() {
        return hasFee == null || !hasFee || (fee != null && fee.compareTo(BigDecimal.ZERO) > 0);
    }
}