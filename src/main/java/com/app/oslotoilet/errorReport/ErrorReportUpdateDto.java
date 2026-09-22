package com.app.oslotoilet.errorReport;

import com.app.oslotoilet.enums.RequestStatus;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorReportUpdateDto {

    private RequestStatus status;

    @Size(max = 255, message = "Admin comment cannot exceed 255 characters")
    private String adminComment;
}
