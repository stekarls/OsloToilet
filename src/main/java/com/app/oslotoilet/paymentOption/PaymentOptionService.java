package com.app.oslotoilet.paymentOption;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentOptionService {

    private final PaymentOptionRepository paymentOptionRepository;

    public PaymentOptionService(PaymentOptionRepository paymentOptionRepository){
        this.paymentOptionRepository = paymentOptionRepository;
    }

    @Transactional(readOnly = true)
    public List<PaymentOptionResponseDto> getAllPaymentOptions(){
        return paymentOptionRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentOptionResponseDto getPaymentOptionById(UUID paymentOptionId){
        return paymentOptionRepository.findById(paymentOptionId)
                .map(this::mapToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Payment option not found with paymentOptionId: " + paymentOptionId));
    }

    public PaymentOptionResponseDto createPaymentOption(PaymentOptionRequestDto dto){
        if (paymentOptionRepository.existsByCode(dto.getCode())) {
            throw new IllegalStateException("Payment option already exists: " + dto.getCode());
        }

        PaymentOption paymentOption = PaymentOption.builder()
                .code(dto.getCode())
                .build();

        return mapToResponseDto(paymentOptionRepository.save(paymentOption));
    }

    public void deletePaymentOption(UUID paymentOptionId){
        if (!paymentOptionRepository.existsById(paymentOptionId)){
            throw new EntityNotFoundException("Payment option not found with paymentOptionId: " + paymentOptionId);
        }
        paymentOptionRepository.deleteById(paymentOptionId);
    }

    private PaymentOptionResponseDto mapToResponseDto(PaymentOption paymentOption){
        return PaymentOptionResponseDto.builder()
                .id(paymentOption.getId())
                .code(paymentOption.getCode())
                .build();
    }
}
