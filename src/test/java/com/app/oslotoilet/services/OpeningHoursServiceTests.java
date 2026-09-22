package com.app.oslotoilet.services;

import com.app.oslotoilet.openingHours.*;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.DayOfWeek.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpeningHoursServiceTests {

    @Mock
    private OpeningHoursRepository openingHoursRepository;

    @Mock
    private ToiletRepository toiletRepository;

    @InjectMocks
    private OpeningHoursService openingHoursService;

    private static final LocalTime EIGHT_AM = LocalTime.of(8, 0);
    private static final LocalTime EIGHT_PM = LocalTime.of(20, 0);
    private static final LocalTime SIX_PM = LocalTime.of(18, 0);
    private static final LocalTime TWO_AM = LocalTime.of(2, 0);

    private UUID toiletId;
    private Toilet toilet;

    @BeforeEach
    void setUp() {
        toiletId = UUID.randomUUID();
        toilet = Toilet.builder().id(toiletId).build();
    }

    private void givenExistingToilet() {
        when(toiletRepository.findById(toiletId)).thenReturn(Optional.of(toilet));
    }

    private OpeningHours hours(DayOfWeek day, LocalTime openingTime, LocalTime closingTime) {
        return OpeningHours.builder()
                .id(UUID.randomUUID())
                .toilet(toilet)
                .dayOfWeek(day)
                .openingTime(openingTime)
                .closingTime(closingTime)
                .build();
    }

    private static List<DayOfWeek> daysOf(List<OpeningHoursResponseDto> openingHours) {
        return openingHours.stream().map(OpeningHoursResponseDto::getDayOfWeek).toList();
    }

    @Nested
    class GetOpeningHours {

        @Test
        void getOpeningHoursForToilet_shouldListDaysFromMondayToSunday_whateverOrderTheyAreStoredIn() {
            givenExistingToilet();
            //Alphabetical, which is the order the database returns when sorting the stored day names
            when(openingHoursRepository.findByToilet(toilet)).thenReturn(List.of(
                    hours(FRIDAY, EIGHT_AM, EIGHT_PM),
                    hours(MONDAY, EIGHT_AM, EIGHT_PM),
                    hours(SUNDAY, EIGHT_AM, EIGHT_PM),
                    hours(WEDNESDAY, EIGHT_AM, EIGHT_PM)));

            List<OpeningHoursResponseDto> result = openingHoursService.getOpeningHoursForToilet(toiletId);

            assertEquals(List.of(MONDAY, WEDNESDAY, FRIDAY, SUNDAY), daysOf(result));
        }
    }

    @Nested
    class AddOpeningHours {

        @Test
        void addOpeningHours_shouldAcceptHoursThatEndAfterMidnight() {
            givenExistingToilet();
            when(openingHoursRepository.existsByToiletAndDayOfWeek(toilet, FRIDAY)).thenReturn(false);
            when(openingHoursRepository.save(any(OpeningHours.class))).thenAnswer(invocation -> invocation.getArgument(0));

            OpeningHoursResponseDto response = openingHoursService.addOpeningHours(toiletId, new OpeningHoursRequestDto(FRIDAY, SIX_PM, TWO_AM));

            assertEquals(SIX_PM, response.getOpeningTime());
            assertEquals(TWO_AM, response.getClosingTime());
        }

        @Test
        void addOpeningHours_shouldReject_andSaveNothing_whenOpeningAndClosingTimeAreEqual() {
            givenExistingToilet();
            OpeningHoursRequestDto request = new OpeningHoursRequestDto(MONDAY, EIGHT_AM, EIGHT_AM);

            assertThrows(IllegalArgumentException.class, () -> openingHoursService.addOpeningHours(toiletId, request));

            verify(openingHoursRepository, never()).save(any());
        }

        @Test
        void addOpeningHours_shouldReject_andSaveNothing_whenToiletIsAlwaysOpen() {
            toilet.setAlwaysOpen(true);
            givenExistingToilet();
            OpeningHoursRequestDto request = new OpeningHoursRequestDto(MONDAY, EIGHT_AM, EIGHT_PM);

            assertThrows(IllegalStateException.class, () -> openingHoursService.addOpeningHours(toiletId, request));

            verify(openingHoursRepository, never()).save(any());
        }
    }

    @Nested
    class AddBulkOpeningHours {

        @Test
        void addBulkOpeningHours_shouldReturnDaysFromMondayToSunday_whateverOrderTheyWereSentIn() {
            givenExistingToilet();
            when(openingHoursRepository.findByToilet(toilet)).thenReturn(List.of());
            when(openingHoursRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
            OpeningHoursBulkRequestDto request = new OpeningHoursBulkRequestDto(List.of(
                    new OpeningHoursRequestDto(SUNDAY, EIGHT_AM, EIGHT_PM),
                    new OpeningHoursRequestDto(MONDAY, EIGHT_AM, EIGHT_PM),
                    new OpeningHoursRequestDto(THURSDAY, SIX_PM, TWO_AM)));

            List<OpeningHoursResponseDto> result = openingHoursService.addBulkOpeningHours(toiletId, request);

            assertEquals(List.of(MONDAY, THURSDAY, SUNDAY), daysOf(result));
        }

        @Test
        void addBulkOpeningHours_shouldReject_andSaveNothing_whenAnyDayHasEqualOpeningAndClosingTime() {
            givenExistingToilet();
            OpeningHoursBulkRequestDto request = new OpeningHoursBulkRequestDto(List.of(
                    new OpeningHoursRequestDto(MONDAY, EIGHT_AM, EIGHT_PM),
                    new OpeningHoursRequestDto(TUESDAY, EIGHT_AM, EIGHT_AM)));

            assertThrows(IllegalArgumentException.class, () -> openingHoursService.addBulkOpeningHours(toiletId, request));

            verify(openingHoursRepository, never()).saveAll(anyList());
        }

        @Test
        void addBulkOpeningHours_shouldReject_andSaveNothing_whenToiletIsAlwaysOpen() {
            toilet.setAlwaysOpen(true);
            givenExistingToilet();
            OpeningHoursBulkRequestDto request = new OpeningHoursBulkRequestDto(List.of(
                    new OpeningHoursRequestDto(MONDAY, EIGHT_AM, EIGHT_PM)));

            assertThrows(IllegalStateException.class, () -> openingHoursService.addBulkOpeningHours(toiletId, request));

            verify(openingHoursRepository, never()).saveAll(anyList());
        }
    }

    @Nested
    class UpdateOpeningHours {

        private OpeningHours existingHours;

        @BeforeEach
        void givenExistingHours() {
            existingHours = hours(MONDAY, EIGHT_AM, EIGHT_PM);
            when(openingHoursRepository.findById(existingHours.getId())).thenReturn(Optional.of(existingHours));
        }

        @Test
        void updateOpeningHours_shouldAcceptHoursThatEndAfterMidnight() {
            OpeningHoursResponseDto response = openingHoursService.updateOpeningHours(toiletId, existingHours.getId(), new OpeningHoursUpdateDto(SIX_PM, TWO_AM));

            assertEquals(SIX_PM, response.getOpeningTime());
            assertEquals(TWO_AM, response.getClosingTime());
        }

        @Test
        void updateOpeningHours_shouldReject_whenOpeningAndClosingTimeAreEqual() {
            OpeningHoursUpdateDto patch = new OpeningHoursUpdateDto(SIX_PM, SIX_PM);

            assertThrows(IllegalArgumentException.class, () -> openingHoursService.updateOpeningHours(toiletId, existingHours.getId(), patch));
        }
    }
}
