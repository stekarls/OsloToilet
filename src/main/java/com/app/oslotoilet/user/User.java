package com.app.oslotoilet.user;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = true)
    private String nickname;

    @Column(name = "contribution_points", nullable = false)
    private Long contributionPoints = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.contributionPoints == null) {
            this.contributionPoints = 0L;
        }
        if (this.createdAt == null){
            this.createdAt = OffsetDateTime.now();
        }
    }

}
