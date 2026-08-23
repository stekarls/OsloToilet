package com.app.oslotoilet.feature;

import com.app.oslotoilet.enums.FeatureCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeatureUpdateDto {
    private String description;
    private FeatureCode featureCode;
}
