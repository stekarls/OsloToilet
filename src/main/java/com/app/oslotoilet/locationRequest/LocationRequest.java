package com.app.oslotoilet.locationRequest;


import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Table(name = "location_requests")
public class LocationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name is required")
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

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @NotBlank(message = "Description is required")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    @NotNull(message = "Request status is required")
    private RequestStatus requestStatus;

    @Column(name = "admin_comment")
    private String adminComment;

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull(message = "Creation timestamp is required")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull(message = "Update timestamp is required")
    private OffsetDateTime updatedAt;

    @Column(name = "has_fee", nullable = false)
    private boolean hasFee;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Fee must be greater than 0")
    private BigDecimal fee;


    //TODO: Is this the most efficient way to update timestamp?
    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = OffsetDateTime.now();
    }
}
