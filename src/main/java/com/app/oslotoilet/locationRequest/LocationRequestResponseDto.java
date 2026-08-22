package com.app.oslotoilet.locationRequest;

import com.app.oslotoilet.enums.RequestStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationRequestResponseDto {
    private UUID id;               // Added for the client
    private UUID userId;           // Safe to return, still hides the full User object
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private RequestStatus requestStatus; // Added so they see it's PENDING
    private boolean hasFee;
    private BigDecimal fee;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;// Added for context
}
