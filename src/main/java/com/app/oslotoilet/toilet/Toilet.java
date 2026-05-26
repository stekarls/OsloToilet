package com.app.oslotoilet.toilet;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
    @Size(min = 5, message = "Name must be 5 characters in length or more")
    @Size(max = 128, message = "Description cannot exceed 128 characters")
    private String name;

    @Column(precision = 9, scale = 6, nullable = false)
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6, nullable = false)
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private BigDecimal longitude;

    @Column(name = "has_fee")
    private boolean hasFee;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal fee;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "has_conditions")
    private boolean hasConditions;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String conditions;

    @Column(name = "is_seasonal")
    private boolean isSeasonal;

    @Column(name = "is_closed")
    private boolean isClosed;

    @Column(updatable = false, nullable = false)
    private OffsetDateTime added;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
