package com.app.oslotoilet.openingHours;

import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
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

        return openingHoursRepository.findByToilet(toilet)
                .stream()
                .sorted(Comparator.comparing(OpeningHours::getDayOfWeek))
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    public OpeningHoursResponseDto addOpeningHours(UUID toiletId, OpeningHoursRequestDto dto) {
        Toilet toilet = toiletRepository.findById(toiletId)
                .orElseThrow(() -> new EntityNotFoundException("Toilet not found with id: " + toiletId));

        validateToiletIsNotAlwaysOpen(toilet);
        validateTimes(dto.getOpeningTime(), dto.getClosingTime());

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

        validateToiletIsNotAlwaysOpen(toilet);
        dto.getOpeningHours().forEach(h -> validateTimes(h.getOpeningTime(), h.getClosingTime()));

        List<DayOfWeek> incomingDays = dto.getOpeningHours().stream()
                .map(OpeningHoursRequestDto::getDayOfWeek)
                .toList();

        Set<DayOfWeek> uniqueDays = new HashSet<>(incomingDays);
        if (uniqueDays.size() != incomingDays.size()) {
            throw new IllegalStateException("Duplicate days in request");
        }

        List<OpeningHours> existing = openingHoursRepository.findByToilet(toilet);
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
                .sorted(Comparator.comparing(OpeningHours::getDayOfWeek))
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

        validateTimes(openingHours.getOpeningTime(), openingHours.getClosingTime());

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

    //A closing time before the opening time is allowed and means the toilet closes after midnight, e.g. 18:00-02:00.
    //Equal times are rejected, since they say nothing, and a toilet open around the clock is marked alwaysOpen instead
    private void validateTimes(LocalTime openingTime, LocalTime closingTime) {
        if (openingTime.equals(closingTime)) {
            throw new IllegalArgumentException("Opening and closing time cannot be the same");
        }
    }

    private void validateToiletIsNotAlwaysOpen(Toilet toilet) {
        if (toilet.isAlwaysOpen()) {
            throw new IllegalStateException("An always open toilet cannot have opening hours");
        }
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
