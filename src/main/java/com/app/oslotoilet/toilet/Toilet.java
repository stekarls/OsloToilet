package com.app.oslotoilet.toilet;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "toilets")
public class Toilet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 128)
    private String name;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Column(name = "has_fee")
    private boolean hasFee;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal fee;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "has_conditions")
    private Boolean hasConditions;

    @Column(columnDefinition = "TEXT")
    private String conditions;

    private boolean season;

    private Boolean closed;
}
