package com.app.oslotoilet.errorReport;


import com.app.oslotoilet.enums.RequestStatus;
import com.app.oslotoilet.toilet.Toilet;
import com.app.oslotoilet.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
    @NotBlank(message = "Description is required")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull(message = "Creation timestamp is required")
    private OffsetDateTime created;

    @Column(name = "updated_at", nullable = false)
    @NotNull(message = "Update timestamp is required")
    private OffsetDateTime updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    @NotNull(message = "Request status is required")
    private RequestStatus status;

    @Column(name = "admin_comment")
    private String adminComment;


}
