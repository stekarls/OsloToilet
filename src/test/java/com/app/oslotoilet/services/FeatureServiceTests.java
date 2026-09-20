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
        verify(featureRepository).findAll();

    }

    @Test
    void getFeatureById_shouldReturnResponseDto(){
        mockFeature.setId(featureId);

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
    void createFeature_shouldSucceed_whenFeatureCodeDoesNotExist(){
        when(featureRepository.existsByFeatureCode(mockFeature.getFeatureCode())).thenReturn(false);
        when(featureRepository.save(any(Feature.class))).thenAnswer(invocation -> {
            Feature featureToSave = invocation.getArgument(0);
            featureToSave.setId(featureId);
            return featureToSave;
        });

        FeatureResponseDto result = featureService.createFeature(
                new FeatureRequestDto(
                        mockFeature.getFeatureCode(),
                        mockFeature.getDescription()
                )
        );

        assertNotNull(result);
        assertEquals(featureId, result.getId());
        verify(featureRepository).existsByFeatureCode(mockFeature.getFeatureCode());
        verify(featureRepository).save(any(Feature.class));
    }

    @Test
    void createFeature_shouldThrow_whenFeatureCodeExists(){
        when(featureRepository.existsByFeatureCode(mockFeature.getFeatureCode())).thenReturn(true);


        assertThrows(IllegalArgumentException.class, () -> featureService.createFeature(
                new FeatureRequestDto(
                        mockFeature.getFeatureCode(),
                        mockFeature.getDescription()
                )
        ));

        verify(featureRepository).existsByFeatureCode(mockFeature.getFeatureCode());
        verify(featureRepository, never()).save(any(Feature.class));
    }

    @Test
    void deleteFeature_shouldSucceed_whenFeatureExists(){
        when(featureRepository.existsById(featureId)).thenReturn(true);

        featureService.deleteFeature(featureId);

        verify(featureRepository).existsById(featureId);
        verify(featureRepository).deleteById(featureId);
    }

    @Test
    void deleteFeature_shouldThrow_whenFeatureDoesNotExist(){
        when(featureRepository.existsById(featureId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> featureService.deleteFeature(featureId));

        verify(featureRepository).existsById(featureId);
        verify(featureRepository, never()).deleteById(featureId);
    }

    @Test
    void updateFeature_shouldUpdateFields_whenFeatureExistsAndCodeIsAvailable() {
        FeatureUpdateDto updateDto = new FeatureUpdateDto(FeatureCode.SHOWERS, "Updated description");

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(mockFeature));
        when(featureRepository.existsByFeatureCode(updateDto.getFeatureCode())).thenReturn(false);
        when(featureRepository.save(any(Feature.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeatureResponseDto result = featureService.updateFeature(featureId, updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getDescription(), result.getDescription());
        assertEquals(updateDto.getFeatureCode(), result.getFeatureCode());
        verify(featureRepository).findById(featureId);
        verify(featureRepository).save(mockFeature);
    }

    @Test
    void updateFeature_shouldThrow_whenFeatureDoesNotExist() {
        FeatureUpdateDto updateDto = new FeatureUpdateDto(FeatureCode.SHOWERS, "Updated description");
        when(featureRepository.findById(featureId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> featureService.updateFeature(featureId, updateDto));

        verify(featureRepository).findById(featureId);
        verify(featureRepository, never()).save(any());
    }

    @Test
    void updateFeature_shouldThrow_whenFeatureCodeAlreadyExists() {
        FeatureUpdateDto updateDto = new FeatureUpdateDto(FeatureCode.AUTOMATIC_DOOR, "Updated description");

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(mockFeature));
        when(featureRepository.existsByFeatureCode(FeatureCode.AUTOMATIC_DOOR)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> featureService.updateFeature(featureId, updateDto));

        verify(featureRepository).findById(featureId);
        verify(featureRepository).existsByFeatureCode(FeatureCode.AUTOMATIC_DOOR);
        verify(featureRepository, never()).save(any());
    }

    @Test
    void updateFeature_shouldSucceedWithoutDuplicateCheck_whenCodeIsUnchanged() {
        FeatureUpdateDto updateDto = new FeatureUpdateDto(FeatureCode.SHOWERS, "Updated description");

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(mockFeature));
        when(featureRepository.save(any(Feature.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeatureResponseDto result = featureService.updateFeature(featureId, updateDto);

        assertEquals(FeatureCode.SHOWERS, result.getFeatureCode());
        assertEquals("Updated description", result.getDescription());
        verify(featureRepository, never()).existsByFeatureCode(any());
        verify(featureRepository).save(mockFeature);
    }

    @Test
    void updateFeature_shouldKeepExistingValues_whenUpdateDtoFieldsAreNull() {
        FeatureUpdateDto updateDto = new FeatureUpdateDto(null, null);

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(mockFeature));
        when(featureRepository.save(any(Feature.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FeatureResponseDto result = featureService.updateFeature(featureId, updateDto);

        assertEquals(FeatureCode.SHOWERS, result.getFeatureCode());
        assertEquals("Shower facilities", result.getDescription());
        verify(featureRepository, never()).existsByFeatureCode(any());
        verify(featureRepository).save(mockFeature);
    }


}
