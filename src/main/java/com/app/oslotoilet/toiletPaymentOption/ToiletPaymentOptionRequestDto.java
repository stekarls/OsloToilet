package com.app.oslotoilet.toiletPaymentOption;


import com.app.oslotoilet.enums.SourceType;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "Payment option ID is required")
    private UUID paymentOptionId;
    @NotNull(message = "Source is required")
    private SourceType source;
}
