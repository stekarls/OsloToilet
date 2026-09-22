package com.app.oslotoilet.errorReport;


import com.app.oslotoilet.enums.RequestStatus;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorReportResponseDto {

    private UUID id;

    private UUID toiletId;

    private UUID userId;

    private String description;

    private OffsetDateTime created;

    private OffsetDateTime updated;

    private RequestStatus status;

    private String adminComment;
}
