package com.app.oslotoilet.toilet;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToiletUpdateDto {
    @Size(min = 5, max = 64, message = "Toilet name must be between 5 and 64 characters")
    private String name;
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal longitude;
    private Boolean hasFee;
    private BigDecimal fee;
    private Boolean alwaysOpen;
    private Boolean seasonal;
    private Boolean closed;

    @Pattern(regexp = "(?s).*\\S.*", message = "Description cannot be blank")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String conditions;

}
