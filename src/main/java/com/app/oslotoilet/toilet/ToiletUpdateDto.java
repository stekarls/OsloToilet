package com.app.oslotoilet.toilet;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToiletUpdateDto {
    @Size(min = 5, message = "Name must be 5 characters in length or more")
    @Size(max = 128, message = "Description cannot exceed 128 characters")
    private String name;
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal longitude;
    private Boolean hasFee;
    private BigDecimal fee;
    private Boolean isSeasonal;
    private Boolean closed;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String conditions;

}
