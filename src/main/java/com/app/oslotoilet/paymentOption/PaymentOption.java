package com.app.oslotoilet.paymentOption;

import com.app.oslotoilet.enums.PaymentCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_options")
public class PaymentOption{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_option", nullable = false, unique = true, length = 32)
    @NotNull(message = "Payment option code is required")
    private PaymentCode code;
}
