package com.app.oslotoilet.toiletFeature;

import com.app.oslotoilet.enums.SourceType;
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

    @NotNull(message = "Source is required")
    private SourceType source;
}
