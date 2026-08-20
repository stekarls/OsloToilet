package com.app.oslotoilet.toiletPaymentOption;


import com.app.oslotoilet.enums.PaymentCode;
import com.app.oslotoilet.enums.SourceType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToiletPaymentOptionResponseDto {
    private UUID id;
    private UUID toiletId;
    private Boolean test;
    private PaymentCode paymentCode;
    private OffsetDateTime verifiedAt;
    private SourceType source;
}
