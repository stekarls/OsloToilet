package com.app.oslotoilet.toiletFeature;


import com.app.oslotoilet.enums.FeatureCode;
import com.app.oslotoilet.enums.SourceType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToiletFeatureResponseDto{
    private UUID id;
    private UUID toiletId;
    private FeatureCode featureCode;
    private SourceType source;
    private OffsetDateTime verified;
}
