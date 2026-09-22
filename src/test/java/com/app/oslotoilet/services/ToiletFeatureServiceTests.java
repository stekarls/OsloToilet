package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.FeatureCode;
import com.app.oslotoilet.enums.PaymentCode;
import com.app.oslotoilet.enums.SourceType;
import com.app.oslotoilet.feature.Feature;
import com.app.oslotoilet.feature.FeatureRepository;
import com.app.oslotoilet.paymentOption.PaymentOption;
import com.app.oslotoilet.paymentOption.PaymentOptionRepository;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import com.app.oslotoilet.toiletFeature.*;
import com.app.oslotoilet.toiletPaymentOption.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//The source says who added the data, and verifiedAt says whether it is confirmed.
//Admins confirm what they add themselves, while user contributions wait for an admin to verify them
@ExtendWith(MockitoExtension.class)
public class ToiletFeatureServiceTests {

    @Mock
    private ToiletRepository toiletRepository;

    private Toilet toilet;

    @BeforeEach
    void setUp() {
        toilet = Toilet.builder().id(UUID.randomUUID()).build();
        when(toiletRepository.findById(toilet.getId())).thenReturn(Optional.of(toilet));
    }

    @Nested
    class Features {

        @Mock
        private FeatureRepository featureRepository;

        @Mock
        private ToiletFeatureRepository toiletFeatureRepository;

        @Captor
        private ArgumentCaptor<List<ToiletFeature>> savedLinks;

        private ToiletFeatureService toiletFeatureService;
        private Feature babyCare;

        @BeforeEach
        void setUp() {
            toiletFeatureService = new ToiletFeatureService(toiletRepository, featureRepository, toiletFeatureRepository);
            babyCare = Feature.builder().id(UUID.randomUUID()).featureCode(FeatureCode.BABY_CARE).build();
        }

        @Test
        void addFeatureToToilet_shouldMarkTheLinkAsAddedAndVerifiedByAdmin() {
            when(featureRepository.findById(babyCare.getId())).thenReturn(Optional.of(babyCare));
            when(toiletFeatureRepository.save(any(ToiletFeature.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ToiletFeatureResponseDto response = toiletFeatureService.addFeatureToToilet(toilet.getId(), new ToiletFeatureRequestDto(babyCare.getId()));

            assertEquals(SourceType.ADMIN, response.getSource());
            assertNotNull(response.getVerified());
        }

        @Test
        void addUserContributedFeatures_shouldMarkTheLinksAsUserContributedAndUnverified() {
            toiletFeatureService.addUserContributedFeatures(toilet.getId(), List.of(babyCare));

            verify(toiletFeatureRepository).saveAll(savedLinks.capture());
            ToiletFeature link = savedLinks.getValue().get(0);
            assertEquals(SourceType.USER_CONTRIBUTION, link.getSource());
            assertNull(link.getVerifiedAt());
            assertSame(toilet, link.getToilet());
        }
    }

    @Nested
    class PaymentOptions {

        @Mock
        private PaymentOptionRepository paymentOptionRepository;

        @Mock
        private ToiletPaymentOptionRepository toiletPaymentOptionRepository;

        @Captor
        private ArgumentCaptor<List<ToiletPaymentOption>> savedLinks;

        private ToiletPaymentOptionService toiletPaymentOptionService;
        private PaymentOption vipps;

        @BeforeEach
        void setUp() {
            toiletPaymentOptionService = new ToiletPaymentOptionService(toiletPaymentOptionRepository, toiletRepository, paymentOptionRepository);
            vipps = PaymentOption.builder().id(UUID.randomUUID()).code(PaymentCode.VIPPS).build();
        }

        @Test
        void addPaymentOption_shouldMarkTheLinkAsAddedAndVerifiedByAdmin() {
            when(paymentOptionRepository.findById(vipps.getId())).thenReturn(Optional.of(vipps));
            when(toiletPaymentOptionRepository.save(any(ToiletPaymentOption.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ToiletPaymentOptionResponseDto response = toiletPaymentOptionService.addPaymentOption(toilet.getId(), new ToiletPaymentOptionRequestDto(vipps.getId()));

            assertEquals(SourceType.ADMIN, response.getSource());
            assertNotNull(response.getVerifiedAt());
        }

        @Test
        void addUserContributedPaymentOptions_shouldMarkTheLinksAsUserContributedAndUnverified() {
            toiletPaymentOptionService.addUserContributedPaymentOptions(toilet.getId(), List.of(vipps));

            verify(toiletPaymentOptionRepository).saveAll(savedLinks.capture());
            ToiletPaymentOption link = savedLinks.getValue().get(0);
            assertEquals(SourceType.USER_CONTRIBUTION, link.getSource());
            assertNull(link.getVerifiedAt());
            assertSame(toilet, link.getToilet());
        }
    }
}
