package com.app.oslotoilet.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    @NotBlank
    @Size(min = 5, max = 12, message = "Nickname must be between 5 and 12 characters")
    private String nickname;
}
