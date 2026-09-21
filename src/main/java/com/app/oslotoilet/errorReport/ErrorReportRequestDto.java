package com.app.oslotoilet.errorReport;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorReportRequestDto {
    @NotNull(message = "Toilet ID is required")
    private UUID toiletID;
    @NotBlank
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    private String description;
}
