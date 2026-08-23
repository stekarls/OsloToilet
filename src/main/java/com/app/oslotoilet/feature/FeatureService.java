package com.app.oslotoilet.feature;

import com.app.oslotoilet.enums.FeatureCode;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository){
        this.featureRepository = featureRepository;
    }

    public List<FeatureResponseDto> getAllFeatures(){
        return featureRepository.findAll().stream().map(this::mapToResponseDto).toList();
    }

    public FeatureResponseDto getFeatureById(UUID featureId){
        Feature feature = featureRepository.findById(featureId).orElseThrow(() -> new EntityNotFoundException("Feature not found with id: " + featureId));
        return mapToResponseDto(feature);
    }

    public FeatureResponseDto createFeature(FeatureRequestDto featureRequestDto){
        FeatureCode featureCode = featureRequestDto.getFeatureCode();
        boolean exists = featureRepository.existsByFeatureCode(featureCode);

        if (exists){
            throw new IllegalArgumentException("Feature with code " + featureCode + " already exists.");
        }

        Feature newFeature = Feature.builder()
                .featureCode(featureCode)
                .description(featureRequestDto.getDescription())
                .build();

        return mapToResponseDto(featureRepository.save(newFeature));
    }

    public void deleteFeature(UUID featureId){
        if (!featureRepository.existsById(featureId)){
            throw new EntityNotFoundException("Feature not found with id: " + featureId);
        }
        featureRepository.deleteById(featureId);
    }

    public FeatureResponseDto updateFeature( UUID featureId, FeatureUpdateDto featureUpdateDto){
        Feature feature = featureRepository.findById(featureId)
                .orElseThrow(() -> new EntityNotFoundException("Feature not found with id: " + featureId));

        if (featureUpdateDto.getDescription() != null) {feature.setDescription(featureUpdateDto.getDescription());}
        if (featureUpdateDto.getFeatureCode() != null) {feature.setFeatureCode(featureUpdateDto.getFeatureCode());}

        return mapToResponseDto(featureRepository.save(feature));
    }

    private FeatureResponseDto mapToResponseDto(Feature feature){
        return new FeatureResponseDto(
                feature.getId(),
                feature.getFeatureCode(),
                feature.getDescription()
        );
    }
}
