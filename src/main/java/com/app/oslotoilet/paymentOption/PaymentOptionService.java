package com.app.oslotoilet.paymentOption;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PaymentOptionService {

    private final PaymentOptionRepository paymentOptionRepository;

    public PaymentOptionService(PaymentOptionRepository paymentOptionRepository){
        this.paymentOptionRepository = paymentOptionRepository;
    }


    public List<PaymentOptionResponseDto> getAllPaymentOptions(){
        return paymentOptionRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public PaymentOptionResponseDto getPaymentOptionById(UUID paymentOptionId){
        return paymentOptionRepository.findById(paymentOptionId)
                .map(this::mapToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Payment option not found with paymentOptionId: " + paymentOptionId));
    }

    private PaymentOptionResponseDto mapToResponseDto(PaymentOption paymentOption){
        return PaymentOptionResponseDto.builder()
                .id(paymentOption.getId())
                .code(paymentOption.getCode())
                .build();
    }
}
