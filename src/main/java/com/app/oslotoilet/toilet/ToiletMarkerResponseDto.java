package com.app.oslotoilet.toilet;


import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToiletMarkerResponseDto {

    private UUID id;

    private String name;

    private boolean hasFee;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Boolean openNow;

}
