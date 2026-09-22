package com.app.oslotoilet.toiletFeature;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToiletFeatureRequestDto{
    @NotNull(message = "Feature ID is required")
    private UUID featureId;
}
