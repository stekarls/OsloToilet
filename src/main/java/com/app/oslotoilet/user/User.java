package com.app.oslotoilet.user;


import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.errorReport.ErrorReport;
import com.app.oslotoilet.locationRequest.LocationRequest;
import com.app.oslotoilet.review.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 12, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 12, message = "Nickname must be between 5 and 12 characters")
    private String nickname;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;


    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "contribution_points", nullable = false)
    @NotNull(message = "Contribution points is required")
    private Long contributionPoints;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "banned", nullable = false)
    @NotNull(message = "Banned status is required")
    private boolean banned;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ErrorReport> errorReports = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LocationRequest> locationRequests = new ArrayList<>();

}
