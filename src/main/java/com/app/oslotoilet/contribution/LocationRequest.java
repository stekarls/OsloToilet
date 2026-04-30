package com.app.oslotoilet.contribution;


import com.app.oslotoilet.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "location_requests")
public class LocationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private RequestStatus requestStatus = RequestStatus.PENDING;

    private String adminComment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = OffsetDateTime.now();
    }
}
