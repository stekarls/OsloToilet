package com.app.oslotoilet.toiletFeature;

import com.app.oslotoilet.enums.SourceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToiletFeatureBulkRequestDto {
    @NotEmpty(message = "Feature IDs cannot be empty")
    private List<@NotNull UUID> featureIds;

    @NotNull(message = "Source is required")
    private SourceType source;
}
