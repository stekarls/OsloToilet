package com.app.oslotoilet.feature;

import com.app.oslotoilet.enums.FeatureCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureUpdateDto {

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotBlank(message = "FeatureCode is required")
    private FeatureCode featureCode;
}
