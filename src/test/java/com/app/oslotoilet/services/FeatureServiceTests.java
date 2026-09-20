package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.FeatureCode;
import com.app.oslotoilet.feature.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeatureServiceTests {

    @Mock
    FeatureRepository featureRepository;

    @InjectMocks
    FeatureService featureService;

    private UUID featureId;
    private Feature mockFeature;


    @BeforeEach
    void setUp() {
        featureId = UUID.randomUUID();
        mockFeature = Feature.builder()
                .id(featureId)
                .featureCode(FeatureCode.SHOWERS)
                .description("Shower facilities")
                .build();

    }

    @Test
    void getAllFeatures_shouldReturnResponseDto(){
        Feature mockFeature2 = Feature.builder()
                .id(UUID.randomUUID())
                .featureCode(FeatureCode.AUTOMATIC_DOOR)
                .description("Automatic door")
                .build();

        when(featureRepository.findAll()).thenReturn(List.of(mockFeature, mockFeature2));

        List<FeatureResponseDto> result = featureService.getAllFeatures();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockFeature.getId(), result.get(0).getId());
        assertEquals(FeatureCode.SHOWERS, result.get(0).getFeatureCode());
        assertEquals("Shower facilities", result.get(0).getDescription());
        assertEquals(mockFeature2.getId(), result.get(1).getId());
        assertEquals(FeatureCode.AUTOMATIC_DOOR, result.get(1).getFeatureCode());
        assertEquals("Automatic door", result.get(1).getDescription());
        verify(featureRepository).findAll();
    }

    @Test
    void getFeatureById_shouldReturnResponseDto(){
        when(featureRepository.findById(featureId)).thenReturn(Optional.of(mockFeature));

        FeatureResponseDto result = featureService.getFeatureById(featureId);

        assertNotNull(result);
        assertEquals(mockFeature.getDescription(), result.getDescription());
        assertEquals(mockFeature.getFeatureCode(), result.getFeatureCode());
        verify(featureRepository).findById(featureId);
    }

    @Test
    void getFeatureById_shouldThrow_whenFeatureNotFound(){
        when(featureRepository.findById(featureId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> featureService.getFeatureById(featureId));

        verify(featureRepository).findById(featureId);
    }

    @Test
    void updateFeature_shouldUpdateDescriptionAndKeepFeatureCode_whenFeatureExists() {
        FeatureUpdateDto updateDto = new FeatureUpdateDto("Updated description");

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(mockFeature));
        when(featureRepository.save(any(Feature.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeatureResponseDto result = featureService.updateFeature(featureId, updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getDescription(), result.getDescription());
        assertEquals(FeatureCode.SHOWERS, result.getFeatureCode());
        verify(featureRepository).findById(featureId);
        verify(featureRepository).save(mockFeature);
    }

    @Test
    void updateFeature_shouldThrow_whenFeatureDoesNotExist() {
        FeatureUpdateDto updateDto = new FeatureUpdateDto("Updated description");
        when(featureRepository.findById(featureId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> featureService.updateFeature(featureId, updateDto));

        verify(featureRepository).findById(featureId);
        verify(featureRepository, never()).save(any());
    }

}
