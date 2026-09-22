package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.FeatureCode;
import com.app.oslotoilet.enums.PaymentCode;
import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.feature.Feature;
import com.app.oslotoilet.feature.FeatureRepository;
import com.app.oslotoilet.locationRequest.*;
import com.app.oslotoilet.paymentOption.PaymentOption;
import com.app.oslotoilet.paymentOption.PaymentOptionRepository;
import com.app.oslotoilet.security.SecurityUser;
import com.app.oslotoilet.toilet.ToiletRequestDto;
import com.app.oslotoilet.toilet.ToiletResponseDto;
import com.app.oslotoilet.toilet.ToiletService;
import com.app.oslotoilet.toiletFeature.ToiletFeatureService;
import com.app.oslotoilet.toiletPaymentOption.ToiletPaymentOptionService;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationRequestServiceTests {

    @Mock
    private LocationRequestRepository locationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ToiletService toiletService;

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private PaymentOptionRepository paymentOptionRepository;

    @Mock
    private ToiletFeatureService toiletFeatureService;

    @Mock
    private ToiletPaymentOptionService toiletPaymentOptionService;

    @InjectMocks
    private LocationRequestService locationRequestService;

    private User user;
    private Feature babyCare;
    private PaymentOption vipps;

    @BeforeEach
    void setUp() {
        user = User.builder().id(UUID.randomUUID()).role(Role.USER).contributionPoints(0L).build();
        babyCare = Feature.builder().id(UUID.randomUUID()).featureCode(FeatureCode.BABY_CARE).build();
        vipps = PaymentOption.builder().id(UUID.randomUUID()).code(PaymentCode.VIPPS).build();
    }

    private LocationRequestDto requestWith(Set<FeatureCode> featureCodes, Set<PaymentCode> paymentCodes) {
        return LocationRequestDto.builder()
                .name("Oslo S toilet")
                .latitude(new BigDecimal("59.910000"))
                .longitude(new BigDecimal("10.750000"))
                .description("By the main entrance")
                .hasFee(false)
                .featureCodes(featureCodes)
                .paymentCodes(paymentCodes)
                .build();
    }

    @Nested
    class CreateLocationRequest {

        @BeforeEach
        void givenExistingUser() {
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        }

        @Test
        void createNewLocationRequest_shouldStoreTheSelectedFeaturesAndPaymentOptions() {
            when(featureRepository.findByFeatureCodeIn(Set.of(FeatureCode.BABY_CARE))).thenReturn(List.of(babyCare));
            when(paymentOptionRepository.findByCodeIn(Set.of(PaymentCode.VIPPS))).thenReturn(List.of(vipps));
            when(locationRequestRepository.save(any(LocationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LocationRequestResponseDto response = locationRequestService.createNewLocationRequest(
                    requestWith(Set.of(FeatureCode.BABY_CARE), Set.of(PaymentCode.VIPPS)), new SecurityUser(user));

            assertEquals(Set.of(FeatureCode.BABY_CARE), response.getFeatureCodes());
            assertEquals(Set.of(PaymentCode.VIPPS), response.getPaymentCodes());
        }

        @Test
        void createNewLocationRequest_shouldAcceptARequestWithoutFeaturesOrPaymentOptions() {
            when(locationRequestRepository.save(any(LocationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

            LocationRequestResponseDto response = locationRequestService.createNewLocationRequest(
                    requestWith(null, null), new SecurityUser(user));

            assertTrue(response.getFeatureCodes().isEmpty());
            assertTrue(response.getPaymentCodes().isEmpty());
        }
    }

    @Nested
    class UpdateRequestStatus {

        private LocationRequest request;
        private UUID newToiletId;

        @BeforeEach
        void givenPendingRequest() {
            request = LocationRequest.builder()
                    .id(UUID.randomUUID())
                    .user(user)
                    .name("Oslo S toilet")
                    .latitude(new BigDecimal("59.910000"))
                    .longitude(new BigDecimal("10.750000"))
                    .description("By the main entrance")
                    .requestStatus(RequestStatus.PENDING)
                    .features(Set.of(babyCare))
                    .paymentOptions(Set.of(vipps))
                    .build();
            newToiletId = UUID.randomUUID();
            when(locationRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        }

        private LocationRequestUpdateDto status(RequestStatus status) {
            return LocationRequestUpdateDto.builder().requestStatus(status).build();
        }

        @Test
        void updateRequestStatus_shouldAddTheSelectedFeaturesAndPaymentOptionsToTheNewToilet_whenApproved() {
            when(toiletService.createToilet(any(ToiletRequestDto.class))).thenReturn(ToiletResponseDto.builder().id(newToiletId).build());

            locationRequestService.updateRequestStatus(request.getId(), status(RequestStatus.APPROVED));

            verify(toiletFeatureService).addUserContributedFeatures(newToiletId, Set.of(babyCare));
            verify(toiletPaymentOptionService).addUserContributedPaymentOptions(newToiletId, Set.of(vipps));
        }

        @Test
        void updateRequestStatus_shouldCreateNothing_whenRejected() {
            locationRequestService.updateRequestStatus(request.getId(), status(RequestStatus.REJECTED));

            verify(toiletService, never()).createToilet(any());
            verify(toiletFeatureService, never()).addUserContributedFeatures(any(), anyCollection());
            verify(toiletPaymentOptionService, never()).addUserContributedPaymentOptions(any(), anyCollection());
        }
    }
}
