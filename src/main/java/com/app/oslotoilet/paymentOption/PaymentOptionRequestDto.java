package com.app.oslotoilet.paymentOption;

import com.app.oslotoilet.enums.PaymentCode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOptionRequestDto{

    @NotNull(message = "Payment code is required")
    private PaymentCode code;
}
