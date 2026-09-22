package com.app.oslotoilet.toilet;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Transactional(readOnly = true)
public class ToiletService {

    private final ToiletRepository toiletRepository;

    public ToiletService(ToiletRepository toiletRepository){
        this.toiletRepository = toiletRepository;
    }

    public List<ToiletResponseDto> findAll(String sort){
        return toiletSortMapper(sort).stream().map(this::mapToResponseDto).toList();
    }

    public ToiletResponseDto findById(UUID id){
        Toilet toilet = toiletRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Toilet not found with ID: " + id));
        return mapToResponseDto(toilet);

    }

    @Transactional
    public ToiletResponseDto createToilet(ToiletRequestDto dto){
        validateToiletClosed(dto.isAlwaysOpen(), dto.isClosed());
        validateFee(dto.getFee(), dto.isHasFee());
        String name = normalizeName(dto.getName());

        if (toiletRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalStateException("A toilet with the name '" + name + "' already exists");
        }

        Toilet toilet = toiletRepository.save(mapToEntity(dto, name));
        return mapToResponseDto(toilet);

    }

    @Transactional
    public ToiletResponseDto updateToilet(ToiletUpdateDto dto, UUID toiletId){
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet not found with ID: " + toiletId));



        if (dto.getName() != null) {
            String name = normalizeName(dto.getName());
            if (toiletRepository.existsByNameIgnoreCaseAndIdNot(name, toiletId)) {
                throw new IllegalStateException("A toilet with the name '" + name + "' already exists");
            }
            toilet.setName(name);
        }
        if (dto.getLatitude() != null) toilet.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) toilet.setLongitude(dto.getLongitude());
        if (dto.getClosed() != null) toilet.setClosed(dto.getClosed());
        if (dto.getAlwaysOpen() != null) toilet.setAlwaysOpen(dto.getAlwaysOpen());
        if (dto.getSeasonal() != null) toilet.setSeasonal(dto.getSeasonal());

        if(dto.getHasFee() != null){
            toilet.setHasFee(dto.getHasFee());
            if (!dto.getHasFee()){
                toilet.setFee(null);
            }
        }
        if (dto.getFee() != null){
            toilet.setFee(dto.getFee());
        }

        if (dto.getDescription() != null) {
            if (dto.getDescription().isBlank()) {
                throw new IllegalArgumentException("A toilet must have a description");
            }
            toilet.setDescription(dto.getDescription());
        }
        if (dto.getConditions() != null) {
            toilet.setConditions(dto.getConditions().isBlank() ? null : dto.getConditions());
        }

        validateToiletClosed(toilet.isAlwaysOpen(), toilet.isClosed());
        validateFee(toilet.getFee(), toilet.isHasFee());

        toiletRepository.saveAndFlush(toilet);
        return mapToResponseDto(toilet);
    }

    @Transactional
    public void deleteToilet(UUID toiletId){
        if (!toiletRepository.existsById(toiletId)){
            throw new EntityNotFoundException("Toilet not found with ID: " + toiletId);
        }
        toiletRepository.deleteById(toiletId);
    }



    private List<Toilet> toiletSortMapper(String sort){
        if (sort != null){
            sort = sort.toLowerCase();
            switch (sort) {
                case "nameasc" -> {
                    return toiletRepository.findAllByOrderByNameAsc();
                }
                case "namedesc" -> {
                    return toiletRepository.findAllByOrderByNameDesc();
                }
                case "createdasc" -> {
                    return toiletRepository.findAllByOrderByAddedAsc();
                }
                case "createddesc" -> {
                    return toiletRepository.findAllByOrderByAddedDesc();
                }
                case "updatedasc" -> {
                    return toiletRepository.findAllByOrderByUpdatedAtAsc();
                }
                case "updateddesc" -> {
                    return toiletRepository.findAllByOrderByUpdatedAtDesc();
                }default -> {
                    return toiletRepository.findAll();
                }
            }
        }
        return toiletRepository.findAll();
    }
    private void validateToiletClosed(boolean alwaysOpen, boolean closed) {
        if (alwaysOpen && closed) {
            throw new IllegalArgumentException("A toilet cannot be both always open and closed");
        }
    }

    private void validateFee(BigDecimal fee, boolean hasFee){
        if (hasFee) {
            if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Fee must be a value greater than 0 when hasFee is true");
            }
        }else {
            if (fee != null) {
                throw new IllegalArgumentException("Fee must be null when hasFee is false");
            }
        }
    }

    private String normalizeName(String name){
        String trimmed = name.trim();
        if (trimmed.length() < 5 || trimmed.length() > 64) {
            throw new IllegalArgumentException("Toilet name must be between 5 and 64 characters");
        }
        return trimmed;
    }

    private Toilet mapToEntity(ToiletRequestDto toiletRequestDto, String name){
        String conditions = toiletRequestDto.getConditions();
        if (conditions != null && conditions.isBlank()) {
            conditions = null;
        }

        return Toilet.builder()
                .name(name)
                .latitude(toiletRequestDto.getLatitude())
                .longitude(toiletRequestDto.getLongitude())
                .hasFee(toiletRequestDto.isHasFee())
                .fee(toiletRequestDto.getFee())
                .description(toiletRequestDto.getDescription())
                .alwaysOpen(toiletRequestDto.isAlwaysOpen())
                .conditions(conditions)
                .isSeasonal(toiletRequestDto.isSeasonal())
                .isClosed(toiletRequestDto.isClosed())
                .added(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
    private ToiletResponseDto mapToResponseDto(Toilet toilet){
        return ToiletResponseDto.builder()
                .id(toilet.getId())
                .name(toilet.getName())
                .latitude(toilet.getLatitude())
                .longitude(toilet.getLongitude())
                .hasFee(toilet.isHasFee())
                .fee(toilet.getFee())
                .description(toilet.getDescription())
                .alwaysOpen(toilet.isAlwaysOpen())
                .hasConditions(toilet.isHasConditions())
                .conditions(toilet.getConditions())
                .isSeasonal(toilet.isSeasonal())
                .isClosed(toilet.isClosed())
                .added(toilet.getAdded())
                .updatedAt(toilet.getUpdatedAt())
                .build();
    }

}
