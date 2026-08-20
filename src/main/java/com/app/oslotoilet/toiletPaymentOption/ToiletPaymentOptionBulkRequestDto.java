package com.app.oslotoilet.toiletPaymentOption;

import com.app.oslotoilet.enums.SourceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToiletPaymentOptionBulkRequestDto {
    @NotEmpty(message = "Payment option IDs cannot be empty")
    private List<@NotNull UUID> paymentOptionIds;
    @NotNull(message = "Source is required")
    private SourceType source;
}
