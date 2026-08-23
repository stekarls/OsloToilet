package com.app.oslotoilet.toiletPaymentOption;


import com.app.oslotoilet.enums.SourceType;
import com.app.oslotoilet.paymentOption.PaymentOption;
import com.app.oslotoilet.toilet.Toilet;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "toilet_has_payment_options", uniqueConstraints = @UniqueConstraint(columnNames = {"toilet_id", "payment_option_id"}))
public class ToiletPaymentOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_option_id", nullable = false)
    private PaymentOption paymentOption;

    @Column(name = "verified")
    private OffsetDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Source is required")
    @Column(name = "source", length = 32, nullable = false)
    private SourceType source;
}