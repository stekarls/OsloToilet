package com.app.oslotoilet.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntryDto {
    private String nickname;
    private Long contributionPoints;
}
