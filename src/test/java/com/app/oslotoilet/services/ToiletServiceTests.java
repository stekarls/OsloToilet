package com.app.oslotoilet.services;

import com.app.oslotoilet.toilet.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToiletServiceTests {

    @Mock
    private ToiletRepository toiletRepository;

    @InjectMocks
    private ToiletService toiletService;

    private static final String NAME = "Grunerlokka Park";
    private static final String OTHER_NAME = "Sofienberg Park";
    private static final BigDecimal LATITUDE = new BigDecimal("59.922800");
    private static final BigDecimal LONGITUDE = new BigDecimal("10.758600");
    private static final BigDecimal FEE = new BigDecimal("20.00");
    private static final String DESCRIPTION = "Public toilet next to the playground";
    private static final String CONDITIONS = "Key available at the kiosk";
    private static final OffsetDateTime CREATED = OffsetDateTime.parse("2025-01-01T12:00:00Z");

    private UUID toiletId;
    private Toilet existingToilet;

    @BeforeEach
    void setUp() {
        toiletId = UUID.randomUUID();
        existingToilet = Toilet.builder()
                .id(toiletId)
                .name(NAME)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .hasFee(true)
                .fee(FEE)
                .description(DESCRIPTION)
                .conditions(CONDITIONS)
                .alwaysOpen(false)
                .isSeasonal(false)
                .isClosed(false)
                .added(CREATED)
                .updatedAt(CREATED)
                .build();
    }

    private static ToiletRequestDto.ToiletRequestDtoBuilder validFreeToiletRequest() {
        return ToiletRequestDto.builder()
                .name(NAME)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .hasFee(false)
                .fee(null)
                .description(DESCRIPTION);
    }

    private void givenExistingToilet() {
        when(toiletRepository.findById(toiletId)).thenReturn(Optional.of(existingToilet));
    }

    private void makeExistingToiletFree() {
        existingToilet.setHasFee(false);
        existingToilet.setFee(null);
    }

    static Stream<Arguments> supportedSortKeys() {
        return Stream.of(
                Arguments.of("nameAsc", (Function<ToiletRepository, List<Toilet>>) ToiletRepository::findAllByOrderByNameAsc),
                Arguments.of("NAMEASC", (Function<ToiletRepository, List<Toilet>>) ToiletRepository::findAllByOrderByNameAsc),
                Arguments.of("nameDesc", (Function<ToiletRepository, List<Toilet>>) ToiletRepository::findAllByOrderByNameDesc),
                Arguments.of("createdAsc", (Function<ToiletRepository, List<Toilet>>) ToiletRepository::findAllByOrderByAddedAsc),
                Arguments.of("createdDesc", (Function<ToiletRepository, List<Toilet>>) ToiletRepository::findAllByOrderByAddedDesc),
                Arguments.of("updatedAsc", (Function<ToiletRepository, List<Toilet>>) ToiletRepository::findAllByOrderByUpdatedAtAsc),
                Arguments.of("updatedDesc", (Function<ToiletRepository, List<Toilet>>) ToiletRepository::findAllByOrderByUpdatedAtDesc)
        );
    }

    @Nested
    class CreateToilet {

        private final UUID generatedId = UUID.randomUUID();

        private void givenNameIsFreeAndSaveSucceeds() {
            when(toiletRepository.existsByNameIgnoreCase(NAME)).thenReturn(false);
            when(toiletRepository.save(any(Toilet.class))).thenAnswer(invocation -> {
                Toilet toilet = invocation.getArgument(0);
                toilet.setId(generatedId);
                return toilet;
            });
        }

        private Toilet savedToilet() {
            ArgumentCaptor<Toilet> captor = ArgumentCaptor.forClass(Toilet.class);
            verify(toiletRepository).save(captor.capture());
            return captor.getValue();
        }

        @Test
        void createToilet_shouldPersistSubmittedFields_andReturnTheSavedToilet() {
            givenNameIsFreeAndSaveSucceeds();
            ToiletRequestDto request = validFreeToiletRequest()
                    .alwaysOpen(true)
                    .isSeasonal(true)
                    .build();

            ToiletResponseDto response = toiletService.createToilet(request);

            Toilet saved = savedToilet();
            assertEquals(NAME, saved.getName());
            assertEquals(LATITUDE, saved.getLatitude());
            assertEquals(LONGITUDE, saved.getLongitude());
            assertEquals(DESCRIPTION, saved.getDescription());
            assertTrue(saved.isAlwaysOpen());
            assertTrue(saved.isSeasonal());
            assertFalse(saved.isClosed());
            assertEquals(generatedId, response.getId());
            assertEquals(NAME, response.getName());
        }

        @Test
        void createToilet_shouldStampAddedAndUpdatedAtWithTheCreationTime() {
            givenNameIsFreeAndSaveSucceeds();
            OffsetDateTime before = OffsetDateTime.now();

            toiletService.createToilet(validFreeToiletRequest().build());

            OffsetDateTime after = OffsetDateTime.now();
            Toilet saved = savedToilet();
            assertNotNull(saved.getAdded());
            assertFalse(saved.getAdded().isBefore(before));
            assertFalse(saved.getAdded().isAfter(after));
            assertNotNull(saved.getUpdatedAt());
            assertFalse(saved.getUpdatedAt().isBefore(saved.getAdded()));
        }

        @Test
        void createToilet_shouldAcceptFreeToilet_whenNoFeeIsGiven() {
            givenNameIsFreeAndSaveSucceeds();

            toiletService.createToilet(validFreeToiletRequest().build());

            Toilet saved = savedToilet();
            assertFalse(saved.isHasFee());
            assertNull(saved.getFee());
        }

        @ParameterizedTest
        @ValueSource(strings = {"0.01", "20.00", "150"})
        void createToilet_shouldAcceptPaidToilet_whenFeeIsPositive(String fee) {
            givenNameIsFreeAndSaveSucceeds();

            toiletService.createToilet(validFreeToiletRequest().hasFee(true).fee(new BigDecimal(fee)).build());

            Toilet saved = savedToilet();
            assertTrue(saved.isHasFee());
            assertEquals(new BigDecimal(fee), saved.getFee());
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"0", "0.00", "-5.00"})
        void createToilet_shouldRejectPaidToilet_andSaveNothing_whenFeeIsMissingOrNotPositive(String fee) {
            ToiletRequestDto request = validFreeToiletRequest()
                    .hasFee(true)
                    .fee(fee == null ? null : new BigDecimal(fee))
                    .build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.createToilet(request));

            verify(toiletRepository, never()).save(any());
        }

        @Test
        void createToilet_shouldRejectFreeToilet_andSaveNothing_whenAFeeIsGiven() {
            ToiletRequestDto request = validFreeToiletRequest().fee(FEE).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.createToilet(request));

            verify(toiletRepository, never()).save(any());
        }

        @Test
        void createToilet_shouldReject_andSaveNothing_whenToiletIsBothAlwaysOpenAndClosed() {
            ToiletRequestDto request = validFreeToiletRequest().alwaysOpen(true).isClosed(true).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.createToilet(request));

            verify(toiletRepository, never()).save(any());
        }

        @Test
        void createToilet_shouldReject_andSaveNothing_whenNameIsAlreadyTaken() {
            when(toiletRepository.existsByNameIgnoreCase(NAME)).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> toiletService.createToilet(validFreeToiletRequest().build()));

            verify(toiletRepository, never()).save(any());
        }

        @Test
        void createToilet_shouldTrimTheName_beforeCheckingAndSavingIt() {
            givenNameIsFreeAndSaveSucceeds();

            toiletService.createToilet(validFreeToiletRequest().name("  " + NAME + "  ").build());

            assertEquals(NAME, savedToilet().getName());
        }

        @ParameterizedTest
        @ValueSource(ints = {5, 64})
        void createToilet_shouldAcceptNames_atTheLengthLimits(int length) {
            String name = "x".repeat(length);
            when(toiletRepository.existsByNameIgnoreCase(name)).thenReturn(false);
            when(toiletRepository.save(any(Toilet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            toiletService.createToilet(validFreeToiletRequest().name(name).build());

            assertEquals(name, savedToilet().getName());
        }

        @ParameterizedTest
        @ValueSource(ints = {4, 65})
        void createToilet_shouldReject_andSaveNothing_whenNameIsOutsideTheLengthLimits(int length) {
            ToiletRequestDto request = validFreeToiletRequest().name("x".repeat(length)).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.createToilet(request));

            verify(toiletRepository, never()).save(any());
        }

        //"  abcd  " is 8 characters, so it passes the request's length check, but only 4 remain after trimming
        @Test
        void createToilet_shouldReject_andSaveNothing_whenNameIsTooShortOnceTrimmed() {
            ToiletRequestDto request = validFreeToiletRequest().name("  abcd  ").build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.createToilet(request));

            verify(toiletRepository, never()).save(any());
        }

        @Test
        void createToilet_shouldMarkToiletAsHavingConditions_whenConditionsAreGiven() {
            givenNameIsFreeAndSaveSucceeds();

            toiletService.createToilet(validFreeToiletRequest().conditions(CONDITIONS).build());

            Toilet saved = savedToilet();
            assertTrue(saved.isHasConditions());
            assertEquals(CONDITIONS, saved.getConditions());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t\n"})
        void createToilet_shouldStoreNoConditions_whenConditionsAreMissingOrBlank(String conditions) {
            givenNameIsFreeAndSaveSucceeds();

            toiletService.createToilet(validFreeToiletRequest().conditions(conditions).build());

            Toilet saved = savedToilet();
            assertFalse(saved.isHasConditions());
            assertNull(saved.getConditions());
        }
    }

    @Nested
    class UpdateToilet {

        @Test
        void updateToilet_shouldThrow_whenToiletDoesNotExist() {
            when(toiletRepository.findById(toiletId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> toiletService.updateToilet(new ToiletUpdateDto(), toiletId));
        }

        @Test
        void updateToilet_shouldLeaveEveryFieldUnchanged_whenPatchIsEmpty() {
            givenExistingToilet();

            ToiletResponseDto response = toiletService.updateToilet(new ToiletUpdateDto(), toiletId);

            assertEquals(NAME, response.getName());
            assertEquals(LATITUDE, response.getLatitude());
            assertEquals(LONGITUDE, response.getLongitude());
            assertTrue(response.isHasFee());
            assertEquals(FEE, response.getFee());
            assertEquals(DESCRIPTION, response.getDescription());
            assertTrue(response.isHasConditions());
            assertEquals(CONDITIONS, response.getConditions());
            assertFalse(response.isAlwaysOpen());
            assertFalse(response.isSeasonal());
            assertFalse(response.isClosed());
            assertEquals(CREATED, response.getAdded());
            assertEquals(CREATED, response.getUpdatedAt());
        }

        @Test
        void updateToilet_shouldApplyEveryProvidedField() {
            givenExistingToilet();
            when(toiletRepository.existsByNameIgnoreCaseAndIdNot(OTHER_NAME, toiletId)).thenReturn(false);
            BigDecimal newLatitude = new BigDecimal("59.913900");
            BigDecimal newLongitude = new BigDecimal("10.752200");
            String newDescription = "Moved to the other side of the park";
            ToiletUpdateDto patch = ToiletUpdateDto.builder()
                    .name(OTHER_NAME)
                    .latitude(newLatitude)
                    .longitude(newLongitude)
                    .seasonal(true)
                    .description(newDescription)
                    .build();

            ToiletResponseDto response = toiletService.updateToilet(patch, toiletId);

            assertEquals(OTHER_NAME, response.getName());
            assertEquals(newLatitude, response.getLatitude());
            assertEquals(newLongitude, response.getLongitude());
            assertTrue(response.isSeasonal());
            assertEquals(newDescription, response.getDescription());
            assertEquals(CREATED, response.getAdded());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        void updateToilet_shouldReject_andSaveNothing_whenDescriptionIsBlank(String blank) {
            givenExistingToilet();
            ToiletUpdateDto patch = ToiletUpdateDto.builder().description(blank).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.updateToilet(patch, toiletId));

            verify(toiletRepository, never()).saveAndFlush(any());
        }

        @Test
        void updateToilet_shouldReject_whenNewNameBelongsToAnotherToilet() {
            givenExistingToilet();
            when(toiletRepository.existsByNameIgnoreCaseAndIdNot(OTHER_NAME, toiletId)).thenReturn(true);
            ToiletUpdateDto patch = ToiletUpdateDto.builder().name(OTHER_NAME).build();

            assertThrows(IllegalStateException.class, () -> toiletService.updateToilet(patch, toiletId));
        }

        @Test
        void updateToilet_shouldAllowResendingTheToiletsOwnName() {
            givenExistingToilet();
            when(toiletRepository.existsByNameIgnoreCaseAndIdNot(NAME, toiletId)).thenReturn(false);

            ToiletResponseDto response = toiletService.updateToilet(ToiletUpdateDto.builder().name(NAME).build(), toiletId);

            assertEquals(NAME, response.getName());
        }

        @Test
        void updateToilet_shouldTrimTheName_beforeCheckingAndSavingIt() {
            givenExistingToilet();
            when(toiletRepository.existsByNameIgnoreCaseAndIdNot(OTHER_NAME, toiletId)).thenReturn(false);

            ToiletResponseDto response = toiletService.updateToilet(ToiletUpdateDto.builder().name("  " + OTHER_NAME + " ").build(), toiletId);

            assertEquals(OTHER_NAME, response.getName());
        }

        @Test
        void updateToilet_shouldReject_andSaveNothing_whenNameIsTooShortOnceTrimmed() {
            givenExistingToilet();
            ToiletUpdateDto patch = ToiletUpdateDto.builder().name("  abcd  ").build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.updateToilet(patch, toiletId));

            verify(toiletRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class UpdateToiletFee {

        @BeforeEach
        void givenExistingPaidToilet() {
            givenExistingToilet();
        }

        @Test
        void updateToilet_shouldMakePaidToiletFree_andClearTheFee_whenHasFeeIsSetToFalse() {
            ToiletResponseDto response = toiletService.updateToilet(ToiletUpdateDto.builder().hasFee(false).build(), toiletId);

            assertFalse(response.isHasFee());
            assertNull(response.getFee());
        }

        @Test
        void updateToilet_shouldChangeTheAmount_andStayPaid_whenOnlyAFeeIsSent() {
            BigDecimal newFee = new BigDecimal("30.00");

            ToiletResponseDto response = toiletService.updateToilet(ToiletUpdateDto.builder().fee(newFee).build(), toiletId);

            assertTrue(response.isHasFee());
            assertEquals(newFee, response.getFee());
        }

        @Test
        void updateToilet_shouldMakeFreeToiletPaid_whenHasFeeAndAnAmountAreSent() {
            makeExistingToiletFree();
            BigDecimal newFee = new BigDecimal("15.00");

            ToiletResponseDto response = toiletService.updateToilet(ToiletUpdateDto.builder().hasFee(true).fee(newFee).build(), toiletId);

            assertTrue(response.isHasFee());
            assertEquals(newFee, response.getFee());
        }

        @Test
        void updateToilet_shouldReject_whenHasFeeIsFalseButAFeeIsSent() {
            ToiletUpdateDto patch = ToiletUpdateDto.builder().hasFee(false).fee(new BigDecimal("10.00")).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.updateToilet(patch, toiletId));
        }

        @Test
        void updateToilet_shouldReject_whenFreeToiletIsMadePaidWithoutAnAmount() {
            makeExistingToiletFree();
            ToiletUpdateDto patch = ToiletUpdateDto.builder().hasFee(true).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.updateToilet(patch, toiletId));
        }

        @Test
        void updateToilet_shouldReject_whenAFeeIsSentForAFreeToiletWithoutHasFee() {
            makeExistingToiletFree();
            ToiletUpdateDto patch = ToiletUpdateDto.builder().fee(new BigDecimal("30.00")).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.updateToilet(patch, toiletId));
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "-5.00"})
        void updateToilet_shouldReject_whenFeeIsNotPositive(String fee) {
            ToiletUpdateDto patch = ToiletUpdateDto.builder().fee(new BigDecimal(fee)).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.updateToilet(patch, toiletId));
        }
    }

    @Nested
    class UpdateToiletOpeningState {

        @BeforeEach
        void givenExistingAlwaysOpenToilet() {
            existingToilet.setAlwaysOpen(true);
            givenExistingToilet();
        }

        @Test
        void updateToilet_shouldReject_whenPatchWouldLeaveToiletBothAlwaysOpenAndClosed() {
            ToiletUpdateDto patch = ToiletUpdateDto.builder().closed(true).build();

            assertThrows(IllegalArgumentException.class, () -> toiletService.updateToilet(patch, toiletId));
        }

        @Test
        void updateToilet_shouldAllowClosingAlwaysOpenToilet_whenTheSamePatchTurnsAlwaysOpenOff() {
            ToiletUpdateDto patch = ToiletUpdateDto.builder().closed(true).alwaysOpen(false).build();

            ToiletResponseDto response = toiletService.updateToilet(patch, toiletId);

            assertTrue(response.isClosed());
            assertFalse(response.isAlwaysOpen());
        }
    }

    @Nested
    class UpdateToiletConditions {

        @BeforeEach
        void givenExistingToiletWithConditions() {
            givenExistingToilet();
        }

        @Test
        void updateToilet_shouldMarkToiletAsHavingConditions_whenConditionsAreAdded() {
            existingToilet.setConditions(null);

            ToiletResponseDto response = toiletService.updateToilet(ToiletUpdateDto.builder().conditions(CONDITIONS).build(), toiletId);

            assertTrue(response.isHasConditions());
            assertEquals(CONDITIONS, response.getConditions());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t\n"})
        void updateToilet_shouldRemoveConditions_whenBlankConditionsAreSent(String blank) {
            ToiletResponseDto response = toiletService.updateToilet(ToiletUpdateDto.builder().conditions(blank).build(), toiletId);

            assertFalse(response.isHasConditions());
            assertNull(response.getConditions());
        }
    }

    @Nested
    class FindToilets {

        @Test
        void findById_shouldReturnToilet_whenItExists() {
            givenExistingToilet();

            ToiletResponseDto response = toiletService.findById(toiletId);

            assertEquals(toiletId, response.getId());
            assertEquals(NAME, response.getName());
        }

        @Test
        void findById_shouldThrow_whenToiletDoesNotExist() {
            when(toiletRepository.findById(toiletId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> toiletService.findById(toiletId));
        }

        //Runs once per row in supportedSortKeys(): a sort key and the repository method that should handle it.
        //Only that method is stubbed, and unstubbed methods return an empty list, so a wrong ordering leaves the result empty
        @ParameterizedTest
        @MethodSource("com.app.oslotoilet.services.ToiletServiceTests#supportedSortKeys")
        void findAll_shouldUseTheMatchingOrdering_regardlessOfCase(String sortKey, Function<ToiletRepository, List<Toilet>> ordering) {
            when(ordering.apply(toiletRepository)).thenReturn(List.of(existingToilet));

            List<ToiletResponseDto> result = toiletService.findAll(sortKey);

            assertEquals(1, result.size());
            assertEquals(NAME, result.get(0).getName());
        }

        //Runs for null, "" and each unknown key. Only the unsorted findAll() is stubbed, so a key that wrongly matched an ordering would return an empty list
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"nameAsk", "priceAsc", "name asc"})
        void findAll_shouldReturnUnsortedList_whenSortKeyIsMissingOrUnknown(String sortKey) {
            when(toiletRepository.findAll()).thenReturn(List.of(existingToilet));

            List<ToiletResponseDto> result = toiletService.findAll(sortKey);

            assertEquals(1, result.size());
        }
    }

    @Nested
    class DeleteToilet {

        @Test
        void deleteToilet_shouldDeleteToilet_whenItExists() {
            when(toiletRepository.existsById(toiletId)).thenReturn(true);

            toiletService.deleteToilet(toiletId);

            verify(toiletRepository).deleteById(toiletId);
        }

        @Test
        void deleteToilet_shouldThrow_andDeleteNothing_whenToiletDoesNotExist() {
            when(toiletRepository.existsById(toiletId)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> toiletService.deleteToilet(toiletId));

            verify(toiletRepository, never()).deleteById(any());
        }
    }
}
