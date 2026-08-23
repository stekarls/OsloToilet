package com.app.oslotoilet.feature;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feature")
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
    @PostMapping
    public ResponseEntity<FeatureResponseDto> createFeature(@RequestBody @Valid FeatureRequestDto featureRequestDto){
        return new ResponseEntity<>(featureService.createFeature(featureRequestDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{featureId}")
    public ResponseEntity<FeatureResponseDto> updateFeature(@PathVariable UUID featureId, FeatureUpdateDto featureUpdateDto){
        return ResponseEntity.ok(featureService.updateFeature(featureId, featureUpdateDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{featureId}")
    public ResponseEntity<Void> deleteFeature(@PathVariable UUID featureId){
        featureService.deleteFeature(featureId);
        return ResponseEntity.noContent().build();
    }
}
