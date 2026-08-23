package com.app.oslotoilet.feature;

import com.app.oslotoilet.enums.FeatureCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureRequestDto {

    @NotNull
    private FeatureCode featureCode;

    @NotNull
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}
