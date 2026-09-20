package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.PaymentCode;
import com.app.oslotoilet.paymentOption.PaymentOption;
import com.app.oslotoilet.paymentOption.PaymentOptionRepository;
import com.app.oslotoilet.paymentOption.PaymentOptionResponseDto;
import com.app.oslotoilet.paymentOption.PaymentOptionService;
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
public class PaymentOptionServiceTests {

    @Mock
    PaymentOptionRepository paymentOptionRepository;

    @InjectMocks
    PaymentOptionService paymentOptionService;

    private UUID paymentOptionId;
    private PaymentOption mockPaymentOption;

    @BeforeEach
    void setUp() {
        paymentOptionId = UUID.randomUUID();
        mockPaymentOption = PaymentOption.builder()
                .id(paymentOptionId)
                .code(PaymentCode.VIPPS)
                .build();
    }

    @Test
    void getAllPaymentOptions_shouldReturnResponseDtos() {
        PaymentOption mockPaymentOption2 = PaymentOption.builder()
                .id(UUID.randomUUID())
                .code(PaymentCode.CARD)
                .build();

        when(paymentOptionRepository.findAll()).thenReturn(List.of(mockPaymentOption, mockPaymentOption2));

        List<PaymentOptionResponseDto> result = paymentOptionService.getAllPaymentOptions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(mockPaymentOption.getId(), result.get(0).getId());
        assertEquals(PaymentCode.VIPPS, result.get(0).getCode());
        assertEquals(mockPaymentOption2.getId(), result.get(1).getId());
        assertEquals(PaymentCode.CARD, result.get(1).getCode());
        verify(paymentOptionRepository).findAll();
    }

    @Test
    void getAllPaymentOptions_shouldReturnEmptyList_whenNoPaymentOptionsExist() {
        when(paymentOptionRepository.findAll()).thenReturn(List.of());

        List<PaymentOptionResponseDto> result = paymentOptionService.getAllPaymentOptions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentOptionRepository).findAll();
    }

    @Test
    void getPaymentOptionById_shouldReturnResponseDto() {
        when(paymentOptionRepository.findById(paymentOptionId)).thenReturn(Optional.of(mockPaymentOption));

        PaymentOptionResponseDto result = paymentOptionService.getPaymentOptionById(paymentOptionId);

        assertNotNull(result);
        assertEquals(paymentOptionId, result.getId());
        assertEquals(PaymentCode.VIPPS, result.getCode());
        verify(paymentOptionRepository).findById(paymentOptionId);
    }

    @Test
    void getPaymentOptionById_shouldThrow_whenPaymentOptionNotFound() {
        when(paymentOptionRepository.findById(paymentOptionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentOptionService.getPaymentOptionById(paymentOptionId));

        verify(paymentOptionRepository).findById(paymentOptionId);
    }
}
