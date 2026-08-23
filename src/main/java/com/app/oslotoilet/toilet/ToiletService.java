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
        validateToiletState(dto.isAlwaysOpen(), dto.isClosed());

        Toilet toilet = toiletRepository.save(mapToEntity(dto));
        return mapToResponseDto(toilet);

    }

    @Transactional
    public ToiletResponseDto updateToilet(ToiletUpdateDto dto, UUID toiletId){
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet not found with ID: " + toiletId));



        if (dto.getName() != null) toilet.setName(dto.getName());
        if (dto.getLatitude() != null) toilet.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) toilet.setLongitude(dto.getLongitude());
        if (dto.getHasFee() != null) toilet.setHasFee(dto.getHasFee());
        if (dto.getFee() != null) toilet.setFee(dto.getFee());
        if (dto.getClosed() != null) toilet.setClosed(dto.getClosed());
        if (dto.getAlwaysOpen() != null) toilet.setAlwaysOpen(dto.getAlwaysOpen());

        validateToiletState(toilet.isAlwaysOpen(), toilet.isClosed());
        validateFee(toilet.getFee(), toilet.isHasFee());

        if (dto.getDescription() != null) {
            if (dto.getDescription().isEmpty()) {
                toilet.setDescription(null);
            } else {
                toilet.setDescription(dto.getDescription());
            }
        }
        if (dto.getConditions() != null) {
            if (dto.getConditions().isEmpty()) {
                toilet.setConditions(null);
            } else {
                toilet.setConditions(dto.getConditions());
            }
        }
        //TODO: Find more efficient way of updating updatedAt field
        toilet.setUpdatedAt(OffsetDateTime.now());
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
    private void validateToiletState(boolean alwaysOpen, boolean closed) {
        if (alwaysOpen && closed) {
            throw new IllegalStateException("A toilet cannot be both always open and closed");
        }
    }
    private void validateFee(BigDecimal fee, boolean hasFee){
        if (hasFee) {
            if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Fee must be a value greater than 0 when hasFee is true");
            }
        }else {
            if (fee != null) {
                throw new IllegalStateException("Fee must be null when hasFee is false");
            }
        }
    }
    private Toilet mapToEntity(ToiletRequestDto toiletRequestDto){

        validateFee(toiletRequestDto.getFee(), toiletRequestDto.isHasFee());


        return Toilet.builder()
                .name(toiletRequestDto.getName())
                .latitude(toiletRequestDto.getLatitude())
                .longitude(toiletRequestDto.getLongitude())
                .hasFee(toiletRequestDto.isHasFee())
                .fee(toiletRequestDto.getFee())
                .description(toiletRequestDto.getDescription())
                .alwaysOpen(toiletRequestDto.isAlwaysOpen())
                .hasConditions(toiletRequestDto.isHasConditions())
                .conditions(toiletRequestDto.getConditions())
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
