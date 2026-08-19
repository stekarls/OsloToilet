package com.app.oslotoilet.openingHours;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpeningHoursBulkRequestDto {
    @NotEmpty(message = "Opening hours list cannot be empty")
    private List<@Valid OpeningHoursRequestDto> openingHours;
}
