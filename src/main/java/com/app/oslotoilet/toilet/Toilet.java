package com.app.oslotoilet.toilet;


import com.app.oslotoilet.errorReport.ErrorReport;
import com.app.oslotoilet.openingHours.OpeningHours;
import com.app.oslotoilet.review.Review;
import com.app.oslotoilet.toiletFeature.ToiletFeature;
import com.app.oslotoilet.toiletPaymentOption.ToiletPaymentOption;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @NotBlank(message = "toilet name is required")
    @Column(length = 64, nullable = false, unique = true)
    @Size(min = 5, max = 64, message = "Toilet name must be between 5 and 64 characters")
    private String name;

    @Column(precision = 9, scale = 6, nullable = false)
    @NotNull(message = "Latitude is required")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6, nullable = false)
    @NotNull(message = "Longitude is required")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;

    @Column(name = "has_fee", nullable = false)
    private boolean hasFee;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Fee must be greater than 0")
    private BigDecimal fee;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "has_conditions", nullable = false)
    private boolean hasConditions;

    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String conditions;

    @Column(name = "always_open", nullable = false)
    private boolean alwaysOpen;

    @Column(name = "is_seasonal", nullable = false)
    private boolean isSeasonal;

    @Column(name = "is_closed",nullable = false)
    private boolean isClosed;

    @Column(name = "added", updatable = false, nullable = false)
    private OffsetDateTime added;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "toilet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ToiletFeature> features = new ArrayList<>();

    @OneToMany(mappedBy = "toilet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ToiletPaymentOption> paymentOptions = new ArrayList<>();

    @OneToMany(mappedBy = "toilet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OpeningHours> openingHours = new ArrayList<>();

    @OneToMany(mappedBy = "toilet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "toilet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ErrorReport> errorReports = new ArrayList<>();

}
