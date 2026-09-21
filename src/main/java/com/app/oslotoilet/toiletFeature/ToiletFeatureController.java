package com.app.oslotoilet.toiletFeature;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/toilets")
public class ToiletFeatureController {
    private final ToiletFeatureService toiletFeatureService;

    public ToiletFeatureController(ToiletFeatureService toiletFeatureService) {
        this.toiletFeatureService = toiletFeatureService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/features")
    public ResponseEntity<List<ToiletFeatureResponseDto>> getAllToiletFeatures() {
        List<ToiletFeatureResponseDto> features = toiletFeatureService.getAllToiletFeatures();
        return ResponseEntity.ok(features);
    }


    @GetMapping("/{toiletId}/features")
    public ResponseEntity<List<ToiletFeatureResponseDto>> getFeaturesForToilet(@PathVariable UUID toiletId) {
        List<ToiletFeatureResponseDto> response = toiletFeatureService.getFeaturesForToilet(toiletId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{toiletId}/features/{toiletFeatureId}/verify")
    public ResponseEntity<ToiletFeatureResponseDto> verifyToiletFeature(@PathVariable UUID toiletId, @PathVariable UUID toiletFeatureId) {
        return ResponseEntity.ok(toiletFeatureService.verifyFeature(toiletId, toiletFeatureId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{toiletId}/features")
    public ResponseEntity<ToiletFeatureResponseDto> addFeature(@PathVariable UUID toiletId, @RequestBody @Valid ToiletFeatureRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toiletFeatureService.addFeatureToToilet(toiletId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{toiletId}/features/batch")
    public ResponseEntity<List<ToiletFeatureResponseDto>> addFeatures(@PathVariable UUID toiletId, @RequestBody @Valid ToiletFeatureBulkRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toiletFeatureService.addFeaturesToToilet(toiletId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{toiletId}/features/{toiletFeatureId}")
    public ResponseEntity<Void> removeFeatureFromToilet(@PathVariable UUID toiletId, @PathVariable UUID toiletFeatureId) {
        toiletFeatureService.removeFeatureFromToilet(toiletId, toiletFeatureId);
        return ResponseEntity.noContent().build();
    }
}
