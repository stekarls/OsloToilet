package com.app.oslotoilet.services;

import com.app.oslotoilet.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void changePassword_shouldThrow_WhenCurrentPasswordIsWrong(){
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .password("hashed-old-password")
                .build();

        ChangePasswordDto dto = new ChangePasswordDto("wrong-password", "new-password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong-password", "hashed-old-password")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.changePassword(userId, dto));
    }

    @Test
    void changePassword_shouldSucceed_whenCurrentPasswordIsCorrect() {
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .password("hashed-old-password")
                .build();

        ChangePasswordDto dto = new ChangePasswordDto("correct-password", "newPassword123");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches("correct-password", "hashed-old-password")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "hashed-old-password")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashed-new-password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto response = userService.changePassword(userId, dto);

        assertEquals("hashed-new-password", existingUser.getPassword());
        verify(userRepository).save(existingUser);
        assertNotNull(response);
    }

    @Test
    void changePassword_shouldThrow_whenNewPasswordSameAsCurrent() {
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .password("hashed-old-password")
                .build();

        ChangePasswordDto dto = new ChangePasswordDto("current-password", "current-password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("current-password", "hashed-old-password")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.changePassword(userId, dto));
        verify(userRepository, never()).save(any());
    }
}
