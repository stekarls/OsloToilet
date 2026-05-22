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
    @Size(min = 5, max = 15, message = "Nickname must be between 3 and 15 characters")
    private String nickname;
}
