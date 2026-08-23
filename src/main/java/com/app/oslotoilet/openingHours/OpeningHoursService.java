package com.app.oslotoilet.openingHours;

import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OpeningHoursService {

    private final OpeningHoursRepository openingHoursRepository;
    private final ToiletRepository toiletRepository;

    public OpeningHoursService(OpeningHoursRepository openingHoursRepository, ToiletRepository toiletRepository) {
        this.openingHoursRepository = openingHoursRepository;
        this.toiletRepository = toiletRepository;
    }

    public List<OpeningHoursResponseDto> getOpeningHoursForToilet(UUID toiletId) {
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet not found with id: " + toiletId));

        return openingHoursRepository.findByToiletOrderByDayOfWeekAsc(toilet)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    public OpeningHoursResponseDto addOpeningHours(UUID toiletId, OpeningHoursRequestDto dto) {
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet not found with id: " + toiletId));

        if (openingHoursRepository.existsByToiletAndDayOfWeek(toilet, dto.getDayOfWeek())) {
            throw new IllegalStateException("Opening hours already exist for this toilet on " + dto.getDayOfWeek());
        }

        OpeningHours openingHours = OpeningHours.builder()
                .toilet(toilet)
                .dayOfWeek(dto.getDayOfWeek())
                .openingTime(dto.getOpeningTime())
                .closingTime(dto.getClosingTime())
                .build();

        return mapToResponseDto(openingHoursRepository.save(openingHours));
    }

    @Transactional
    public List<OpeningHoursResponseDto> addBulkOpeningHours(UUID toiletId, OpeningHoursBulkRequestDto dto){
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet not found with id: " + toiletId));

        List<DayOfWeek> incomingDays = dto.getOpeningHours().stream()
                .map(OpeningHoursRequestDto::getDayOfWeek)
                .toList();

        Set<DayOfWeek> uniqueDays = new HashSet<>(incomingDays);
        if (uniqueDays.size() != incomingDays.size()) {
            throw new IllegalStateException("Duplicate days in request");
        }

        List<OpeningHours> existing = openingHoursRepository.findByToiletOrderByDayOfWeekAsc(toilet);
        Set<DayOfWeek> existingDays = existing.stream()
                .map(OpeningHours::getDayOfWeek)
                .collect(Collectors.toSet());

        List<DayOfWeek> conflicts = incomingDays.stream()
                .filter(existingDays::contains)
                .toList();

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Opening hours already exist for days: " + conflicts);
        }

        List<OpeningHours> toSave = dto.getOpeningHours().stream()
                .map(h -> OpeningHours.builder()
                        .toilet(toilet)
                        .dayOfWeek(h.getDayOfWeek())
                        .openingTime(h.getOpeningTime())
                        .closingTime(h.getClosingTime())
                        .build())
                .toList();

        return openingHoursRepository.saveAll(toSave)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    public OpeningHoursResponseDto updateOpeningHours(UUID toiletId, UUID openingHoursId, OpeningHoursUpdateDto dto) {
        OpeningHours openingHours = openingHoursRepository.findById(openingHoursId)
                .orElseThrow(() -> new EntityNotFoundException("Opening hours not found with id: " + openingHoursId));

        if (!openingHours.getToilet().getId().equals(toiletId)) {
            throw new AccessDeniedException("Opening hours do not belong to this toilet");
        }

        if (dto.getOpeningTime() != null) openingHours.setOpeningTime(dto.getOpeningTime());
        if (dto.getClosingTime() != null) openingHours.setClosingTime(dto.getClosingTime());

        return mapToResponseDto(openingHours);
    }

    @Transactional
    public void deleteOpeningHours(UUID toiletId, UUID openingHoursId) {
        OpeningHours openingHours = openingHoursRepository.findById(openingHoursId)
                .orElseThrow(() -> new EntityNotFoundException("Opening hours not found with id: " + openingHoursId));

        if (!openingHours.getToilet().getId().equals(toiletId)) {
            throw new AccessDeniedException("Opening hours do not belong to this toilet");
        }

        openingHoursRepository.delete(openingHours);
    }

    private OpeningHoursResponseDto mapToResponseDto(OpeningHours oh) {
        return OpeningHoursResponseDto.builder()
                .id(oh.getId())
                .toiletId(oh.getToilet().getId())
                .dayOfWeek(oh.getDayOfWeek())
                .openingTime(oh.getOpeningTime())
                .closingTime(oh.getClosingTime())
                .build();
    }
}
