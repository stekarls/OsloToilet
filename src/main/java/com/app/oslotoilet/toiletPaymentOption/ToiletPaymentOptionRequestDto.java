package com.app.oslotoilet.toiletPaymentOption;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToiletPaymentOptionRequestDto {
    @NotNull(message = "Payment option ID is required")
    private UUID paymentOptionId;
}
