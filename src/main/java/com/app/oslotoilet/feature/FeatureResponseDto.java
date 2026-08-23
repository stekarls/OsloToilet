package com.app.oslotoilet.feature;

import com.app.oslotoilet.enums.FeatureCode;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeatureResponseDto {
    private UUID id;
    private FeatureCode featureCode;
    private String description;
}
