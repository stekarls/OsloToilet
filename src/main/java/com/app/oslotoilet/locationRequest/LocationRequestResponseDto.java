package com.app.oslotoilet.locationRequest;

import com.app.oslotoilet.enums.FeatureCode;
import com.app.oslotoilet.enums.PaymentCode;
import com.app.oslotoilet.enums.RequestStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationRequestResponseDto {
    private UUID id;
    private UUID userId;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private RequestStatus requestStatus;
    private boolean hasFee;
    private BigDecimal fee;
    private Set<FeatureCode> featureCodes;
    private Set<PaymentCode> paymentCodes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;// Added for context
}
