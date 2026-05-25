package com.app.oslotoilet.errorReport;


import com.app.oslotoilet.contribution.RequestStatus;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "error_reports")
public class ErrorReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toilet_id", nullable = false)
    private Toilet toilet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime created;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private RequestStatus status;

    @Column(name = "admin_comment")
    private String adminComment;


}
