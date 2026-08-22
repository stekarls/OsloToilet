package com.app.oslotoilet.toiletFeature;

import com.app.oslotoilet.feature.Feature;
import com.app.oslotoilet.feature.FeatureRepository;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ToiletFeatureService {

    private final ToiletRepository toiletRepository;
    private final FeatureRepository featureRepository;
    private final ToiletFeatureRepository toiletFeatureRepository;

    public ToiletFeatureService(ToiletRepository toiletRepository, FeatureRepository featureRepository, ToiletFeatureRepository toiletFeatureRepository) {
        this.toiletRepository = toiletRepository;
        this.featureRepository = featureRepository;
        this.toiletFeatureRepository = toiletFeatureRepository;
    }

    public List<ToiletFeatureResponseDto> getAllToiletFeatures(){
        return toiletFeatureRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ToiletFeatureResponseDto> getFeaturesForToilet(UUID toiletId) {
        Toilet toilet = toiletRepository.findById(toiletId).orElseThrow(() -> new EntityNotFoundException("Toilet not found with id: " + toiletId));

        return toiletFeatureRepository.findByToilet(toilet)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public ToiletFeatureResponseDto addFeatureToToilet(UUID toiletId, ToiletFeatureRequestDto dto) {
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet not found with id: " + toiletId));
        Feature feature = featureRepository.findById(dto.getFeatureId())
                .orElseThrow(() -> new EntityNotFoundException("Feature not found with id: " + dto.getFeatureId()));

        if (toiletFeatureRepository.existsByToiletAndFeature(toilet, feature)) {
            throw new IllegalStateException("Toilet already has feature: " + feature.getCode());
        }

        ToiletFeature toiletFeature = ToiletFeature.builder()
                .toilet(toilet)
                .feature(feature)
                .source(dto.getSource())
                .build();

        return mapToResponseDto(toiletFeatureRepository.save(toiletFeature));
    }

    public List<ToiletFeatureResponseDto> addFeaturesToToilet(UUID toiletId, ToiletFeatureBulkRequestDto dto) {
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet not found with id: " + toiletId));

        List<UUID> featureIds = dto.getFeatureIds();

        List<Feature> features = featureRepository.findAllById(featureIds);

        //TODO: Tell client which features were not found
        if (features.size() != featureIds.size()){
            throw new EntityNotFoundException("One or more features not found");
        }

        List<ToiletFeature> existingFeatures = toiletFeatureRepository.findByToilet(toilet);

        //Extract all existing feature IDs for the toilet
        Set<UUID> existingFeatureIds = existingFeatures.stream()
                .map(tf -> tf.getFeature().getId())
                .collect(Collectors.toSet());

        // Filter out features that are already associated with the toilet
        List<ToiletFeature> toSave = features.stream()
                .filter(f -> !existingFeatureIds.contains(f.getId()))
                .map(f -> ToiletFeature.builder()
                        .toilet(toilet)
                        .feature(f)
                        .source(dto.getSource())
                        .build())
                .toList();

        if (toSave.isEmpty()) {
            throw new IllegalStateException("Toilet already has all provided features");
        }

        return toiletFeatureRepository.saveAll(toSave)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public ToiletFeatureResponseDto verifyFeature(UUID toiletId, UUID toiletFeatureId) {
        ToiletFeature link = toiletFeatureRepository.findById(toiletFeatureId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet-feature link not found with id: " + toiletFeatureId));

        if (!link.getToilet().getId().equals(toiletId)) {
            throw new AccessDeniedException("Toilet-featrue link does not belong to toilet with id: " + toiletId);
        }

        link.setVerified(OffsetDateTime.now());
        return mapToResponseDto(link);
    }

    public void removeFeatureFromToilet(UUID toiletId, UUID featureId) {
        ToiletFeature link = toiletFeatureRepository.findById(featureId)
                .orElseThrow(() -> new EntityNotFoundException("Feature link not found with id: " + featureId));

        if (!link.getToilet().getId().equals(toiletId)) {
            throw new AccessDeniedException("Toilet-feature link does not belong to this toilet");
        }

        toiletFeatureRepository.delete(link);
    }

    private ToiletFeatureResponseDto mapToResponseDto(ToiletFeature tf) {
        return ToiletFeatureResponseDto.builder()
                .id(tf.getId())
                .toiletId(tf.getToilet().getId())
                .featureCode(tf.getFeature().getCode())
                .source(tf.getSource())
                .verified(tf.getVerified())
                .build();
    }


}
