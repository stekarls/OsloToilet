package com.app.oslotoilet.user;

import com.app.oslotoilet.enums.Role;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private UUID id;
    private String nickname;
    private OffsetDateTime createdAt;
    private Long contributionPoints;
    private Role role;
}
