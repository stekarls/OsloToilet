package com.app.oslotoilet.toilet;


import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToiletResponseDto {

    private UUID id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean hasFee;
    private BigDecimal fee;
    private String description;
    private boolean alwaysOpen;
    private boolean hasConditions;
    private String conditions;
    private boolean isSeasonal;
    private boolean isClosed;
    private OffsetDateTime added;
    private OffsetDateTime updatedAt;
}
