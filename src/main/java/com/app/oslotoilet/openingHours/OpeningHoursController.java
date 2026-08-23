package com.app.oslotoilet.openingHours;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/toilets")
public class OpeningHoursController {

    private final OpeningHoursService openingHoursService;

    public OpeningHoursController(OpeningHoursService openingHoursService) {
        this.openingHoursService = openingHoursService;
    }

    @GetMapping("/{toiletId}/opening-hours")
    public ResponseEntity<List<OpeningHoursResponseDto>> getOpeningHours(
            @PathVariable UUID toiletId) {
        return ResponseEntity.ok(openingHoursService.getOpeningHoursForToilet(toiletId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{toiletId}/opening-hours")
    public ResponseEntity<OpeningHoursResponseDto> addOpeningHours(@PathVariable UUID toiletId, @RequestBody @Valid OpeningHoursRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(openingHoursService.addOpeningHours(toiletId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{toiletId}/opening-hours/batch")
    public ResponseEntity<List<OpeningHoursResponseDto>> addBulkOpeningHours(
            @PathVariable UUID toiletId,
            @RequestBody @Valid OpeningHoursBulkRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(openingHoursService.addBulkOpeningHours(toiletId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{toiletId}/opening-hours/{openingHoursId}")
    public ResponseEntity<OpeningHoursResponseDto> updateOpeningHours(
            @PathVariable UUID toiletId,
            @PathVariable UUID openingHoursId,
            @RequestBody @Valid OpeningHoursUpdateDto dto) {
        return ResponseEntity.ok(openingHoursService.updateOpeningHours(toiletId, openingHoursId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{toiletId}/opening-hours/{openingHoursId}")
    public ResponseEntity<Void> deleteOpeningHours(@PathVariable UUID toiletId, @PathVariable UUID openingHoursId) {
        openingHoursService.deleteOpeningHours(toiletId, openingHoursId);
        return ResponseEntity.noContent().build();
    }
}