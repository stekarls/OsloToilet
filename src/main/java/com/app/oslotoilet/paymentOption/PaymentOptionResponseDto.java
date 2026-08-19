package com.app.oslotoilet.paymentOption;

import com.app.oslotoilet.enums.PaymentCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOptionResponseDto{

    private UUID id;
    private PaymentCode code;
}
