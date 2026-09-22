package com.app.oslotoilet.locationRequest;

import com.app.oslotoilet.enums.RequestStatus;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequestUpdateDto {

    private RequestStatus requestStatus;

    @Size(max = 255, message = "Admin comment cannot exceed 255 characters")
    private String adminComment;
}
