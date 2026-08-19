package com.app.oslotoilet.openingHours;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpeningHoursUpdateDto {
    private LocalTime openingTime;
    private LocalTime closingTime;
}
