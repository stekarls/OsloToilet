package com.app.oslotoilet.feature;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/features")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService){
        this.featureService = featureService;
    }

    @GetMapping
    public ResponseEntity<List<FeatureResponseDto>> getAllFeatures(){
        return ResponseEntity.ok(featureService.getAllFeatures());
    }

    @GetMapping("/{featureId}")
    public ResponseEntity<FeatureResponseDto> getFeatureById(@PathVariable UUID featureId){
        return ResponseEntity.ok(featureService.getFeatureById(featureId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{featureId}")
    public ResponseEntity<FeatureResponseDto> updateFeature(@PathVariable UUID featureId, @RequestBody @Valid FeatureUpdateDto featureUpdateDto){
        return ResponseEntity.ok(featureService.updateFeature(featureId, featureUpdateDto));
    }
}
