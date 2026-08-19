package com.app.oslotoilet.paymentOption;

import com.app.oslotoilet.enums.PaymentCode;
import jakarta.persistence.*;
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
    @Column(name = "payment_option", nullable = false, unique = true, length = 50)
    private PaymentCode code;
}
