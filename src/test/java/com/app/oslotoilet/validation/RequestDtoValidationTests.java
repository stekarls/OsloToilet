package com.app.oslotoilet.validation;

import com.app.oslotoilet.locationRequest.LocationRequestDto;
import com.app.oslotoilet.toilet.ToiletRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

//The client must always say whether a toilet has a fee, a missing value is never read as "free"
public class RequestDtoValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static <T> Set<String> invalidFields(Set<ConstraintViolation<T>> violations) {
        return violations.stream().map(v -> v.getPropertyPath().toString()).collect(java.util.stream.Collectors.toSet());
    }

    @Test
    void locationRequest_shouldRequireHasFee() {
        LocationRequestDto request = LocationRequestDto.builder()
                .name("Oslo S toilet")
                .latitude(new BigDecimal("59.910000"))
                .longitude(new BigDecimal("10.750000"))
                .description("By the main entrance")
                .build();

        assertEquals(Set.of("hasFee"), invalidFields(validator.validate(request)));
    }

    @Test
    void toiletRequest_shouldRequireHasFee() {
        ToiletRequestDto request = ToiletRequestDto.builder()
                .name("Oslo S toilet")
                .latitude(new BigDecimal("59.910000"))
                .longitude(new BigDecimal("10.750000"))
                .description("By the main entrance")
                .build();

        assertEquals(Set.of("hasFee"), invalidFields(validator.validate(request)));
    }
}
