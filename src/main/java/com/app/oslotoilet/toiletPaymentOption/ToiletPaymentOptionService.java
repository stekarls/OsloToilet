package com.app.oslotoilet.toiletPaymentOption;

import com.app.oslotoilet.paymentOption.PaymentOption;
import com.app.oslotoilet.paymentOption.PaymentOptionRepository;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.toilet.ToiletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class ToiletPaymentOptionService {

    private final ToiletPaymentOptionRepository toiletPaymentOptionRepository;
    private final ToiletRepository toiletRepository;
    private final PaymentOptionRepository paymentOptionRepository;

    public ToiletPaymentOptionService(ToiletPaymentOptionRepository toiletPaymentOptionRepository, ToiletRepository toiletRepository, PaymentOptionRepository paymentOptionRepository) {
        this.toiletPaymentOptionRepository = toiletPaymentOptionRepository;
        this.toiletRepository = toiletRepository;
        this.paymentOptionRepository = paymentOptionRepository;
    }
    @Transactional(readOnly = true)
    public List<ToiletPaymentOptionResponseDto> getAllToiletPaymentOptions(){
        return toiletPaymentOptionRepository.findAll().stream().map(this::mapToResponseDto).toList();
    }
    @Transactional(readOnly = true)
    public List<ToiletPaymentOptionResponseDto> getPaymentOptionsForToilet(UUID toiletId){
        Toilet toilet = toiletRepository.findById(toiletId).orElseThrow(() -> new EntityNotFoundException("Toilet not found with id " + toiletId));
        return toiletPaymentOptionRepository.findByToilet(toilet).stream().map(this::mapToResponseDto).toList();
    }

    public ToiletPaymentOptionResponseDto addPaymentOption(UUID toiletId, ToiletPaymentOptionRequestDto dto){
        Toilet toilet = toiletRepository.findById(toiletId).orElseThrow(() -> new EntityNotFoundException("Toilet not found with id " + toiletId));
        UUID paymentOptionId = dto.getPaymentOptionId();
        PaymentOption paymentOption = paymentOptionRepository.findById(paymentOptionId).orElseThrow(() -> new EntityNotFoundException("Payment option not found with id: " + paymentOptionId));

        if (toiletPaymentOptionRepository.existsByToiletAndPaymentOption(toilet, paymentOption)) {
            throw new IllegalStateException("Payment option " + paymentOption.getCode() + " already exists for this toilet");
        }

        ToiletPaymentOption toiletPaymentOption = ToiletPaymentOption.builder()
                .toilet(toilet)
                .paymentOption(paymentOption)
                .source(dto.getSource())
                .build();

        return mapToResponseDto(toiletPaymentOptionRepository.save(toiletPaymentOption));
    }

    public List<ToiletPaymentOptionResponseDto> addPaymentOptions(UUID toiletId, ToiletPaymentOptionBulkRequestDto dto){
        Toilet toilet = toiletRepository.findById(toiletId).orElseThrow(() -> new EntityNotFoundException("Toilet not found with id " + toiletId));

        List<UUID> paymentOptionIds = dto.getPaymentOptionIds();

        List<PaymentOption> paymentOptions = paymentOptionRepository.findAllById(paymentOptionIds);

        //TODO: give response which payment option does not exist
        if (paymentOptions.size() != paymentOptionIds.size()) {
            throw new EntityNotFoundException("One or more provided payment options do not exist");
        }

        List<ToiletPaymentOption> existingPaymentOptions = toiletPaymentOptionRepository.findByToilet(toilet);


        Set<UUID> existingPaymentOptionIds = existingPaymentOptions.stream()
                .map(po -> po.getPaymentOption().getId())
                .collect(Collectors.toSet());

        List<ToiletPaymentOption> toSave = paymentOptions.stream()
                .filter(po -> !existingPaymentOptionIds.contains(po.getId()))
                .map(po -> ToiletPaymentOption.builder()
                        .toilet(toilet)
                        .paymentOption(po)
                        .source(dto.getSource())
                        .build())
                .toList();

        if (toSave.isEmpty()) {
            throw new IllegalStateException("Toilet with id " + toiletId + "already has all the specified payment options");
        }

        return toiletPaymentOptionRepository.saveAll(toSave).stream().map(this::mapToResponseDto).toList();
    }

    public ToiletPaymentOptionResponseDto verifyPaymentOption(UUID toiletId, UUID toiletPaymentOptionId){
        ToiletPaymentOption link = toiletPaymentOptionRepository.findById(toiletPaymentOptionId).orElseThrow(() -> new EntityNotFoundException("Toilet-payment link not found with id " + toiletPaymentOptionId));

        if (!link.getToilet().getId().equals(toiletId)){
            throw new AccessDeniedException("Payment option does not belong to the specified toilet");
        }
        link.setVerifiedAt(OffsetDateTime.now());
        return mapToResponseDto(link);
    }

    public void removePaymentOptionFromToilet(UUID toiletId, UUID toiletPaymentOptionId){
        ToiletPaymentOption link = toiletPaymentOptionRepository.findById(toiletPaymentOptionId).orElseThrow(() -> new EntityNotFoundException("Toilet-payment link not found with id " + toiletPaymentOptionId));

        if (!link.getToilet().getId().equals(toiletId)){
            throw new AccessDeniedException("Toilet-payment link id does not belong to the specified toilet");
        }
        toiletPaymentOptionRepository.delete(link);
    }

    private ToiletPaymentOptionResponseDto mapToResponseDto(ToiletPaymentOption toiletPaymentOption){
        return ToiletPaymentOptionResponseDto.builder()
                .id(toiletPaymentOption.getId())
                .toiletId(toiletPaymentOption.getToilet().getId())
                .paymentCode(toiletPaymentOption.getPaymentOption().getCode())
                .verifiedAt(toiletPaymentOption.getVerifiedAt())
                .build();
    }


}
