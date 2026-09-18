package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User mockUser;

    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();
        mockUser = User.builder()
                .id(userId)
                .nickname("TestUser")
                .email("test@example.com")
                .password("hashed-old-password")
                .role(Role.USER)
                .contributionPoints(10L)
                .createdAt(OffsetDateTime.now())
                .banned(false)
                .build();
    }

    @Nested
    class UpdateNicknameByIdTests {

        @Test
        void updateNickname_shouldSucceed_whenUpdatedNicknameIsAvailable() {
            UserUpdateDto dto = new UserUpdateDto("updated-nickname");
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(userRepository.existsByNicknameAndIdNot("updated-nickname", userId)).thenReturn(false);

            UserResponseDto result = userService.updateNicknameById(userId, dto);

            assertEquals("updated-nickname", result.getNickname());

        }

        @Test
        void updateNickname_shouldThrow_whenUpdatedNicknameIsTaken() {
            UserUpdateDto dto = new UserUpdateDto("updated-nickname");
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(userRepository.existsByNicknameAndIdNot("updated-nickname", userId)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> userService.updateNicknameById(userId, dto));


        }
    }

    @Nested
    class ChangePasswordTests {
        @Test
        void changePassword_shouldThrow_WhenCurrentPasswordIsWrong(){


            ChangePasswordDto dto = new ChangePasswordDto("wrong-password", "new-password");

            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches("wrong-password", "hashed-old-password")).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> userService.changePassword(userId, dto));
            verify(userRepository, never()).save(mockUser);
        }

        @Test
        void changePassword_shouldSucceed_whenCurrentPasswordIsCorrect() {


            ChangePasswordDto dto = new ChangePasswordDto("correct-password", "new-password");

            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

            when(passwordEncoder.matches("correct-password", "hashed-old-password")).thenReturn(true);
            when(passwordEncoder.matches("new-password", "hashed-old-password")).thenReturn(false);
            when(passwordEncoder.encode("new-password")).thenReturn("hashed-new-password");

            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            UserResponseDto response = userService.changePassword(userId, dto);

            assertEquals("hashed-new-password", mockUser.getPassword());
            verify(userRepository).save(mockUser);
            assertNotNull(response);
        }

        @Test
        void changePassword_shouldThrow_whenNewPasswordSameAsCurrent() {

            ChangePasswordDto dto = new ChangePasswordDto("current-password", "current-password");

            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches("current-password", "hashed-old-password")).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> userService.changePassword(userId, dto));
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class BanUserTests {

        @Test
        void banUser_Success() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            userService.banUser(userId);
            assertTrue(mockUser.isBanned());
        }

        @Test
        void unBanUser_success() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            userService.unBanUser(userId);
            assertFalse(mockUser.isBanned());
        }
    }

}
