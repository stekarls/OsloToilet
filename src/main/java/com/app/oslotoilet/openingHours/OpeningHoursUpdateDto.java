package com.app.oslotoilet.openingHours;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpeningHoursUpdateDto {
    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;
    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;
}
