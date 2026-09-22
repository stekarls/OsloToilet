package com.app.oslotoilet.services;

import com.app.oslotoilet.auth.AuthResponseDto;
import com.app.oslotoilet.auth.AuthService;
import com.app.oslotoilet.auth.LoginRequestDto;
import com.app.oslotoilet.auth.RegisterRequestDto;
import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.security.JwtService;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Nested
    class RegisterTests{

        private RegisterRequestDto registerRequest;

        @BeforeEach
        void setUp(){
            registerRequest = new RegisterRequestDto(
                    "Alice",
                    "alice@example.com",
                    "rawPassword123"
            );
        }

        @Test
        void register_shouldSucceed_whenEmailAndNicknameAreUnique() {
            UUID generatedUserId = UUID.randomUUID();

            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(userRepository.existsByNickname(registerRequest.getNickname())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(generatedUserId);
                return user;
            });
            when(jwtService.generateToken(generatedUserId.toString())).thenReturn("mocked-jwt-token");

            AuthResponseDto response = authService.register(registerRequest);

            assertNotNull(response);
            assertEquals("mocked-jwt-token", response.getToken());

            verify(passwordEncoder).encode("rawPassword123");
            verify(userRepository).save(any(User.class));
            verify(jwtService).generateToken(generatedUserId.toString());
        }

        @Test
        void register_shouldThrow_whenEmailAlreadyExists() {
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> authService.register(registerRequest));

            verify(userRepository, never()).existsByNickname(anyString());
            verify(userRepository, never()).save(any(User.class));
            verifyNoInteractions(passwordEncoder, jwtService);
        }

        @Test
        void register_shouldThrow_whenNicknameAlreadyExists() {
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(userRepository.existsByNickname(registerRequest.getNickname())).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> authService.register(registerRequest));

            verify(userRepository, never()).save(any(User.class));
            verifyNoInteractions(passwordEncoder, jwtService);
        }

        @Test
        void register_shouldHashPassword_beforeSavingUser() {
            UUID generatedUserId = UUID.randomUUID();
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(userRepository.existsByNickname(registerRequest.getNickname())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(generatedUserId);
                return user;
            });
            when(jwtService.generateToken(generatedUserId.toString())).thenReturn("mocked-jwt-token");

            authService.register(registerRequest);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("encoded-password", userCaptor.getValue().getPassword());
        }

    }

    @Nested
    class LoginTests {

        private LoginRequestDto loginRequestDto;

        private User mockUser;

        private UUID userId;

        @BeforeEach
        void setUp() {
            loginRequestDto = new LoginRequestDto(
                    "alice@example.com",
                    "current-password"
            );
            userId = UUID.randomUUID();
            mockUser = User.builder()
                    .id(userId)
                    .nickname("Alice")
                    .email("alice@example.com")
                    .password("encoded-password")
                    .role(Role.USER)
                    .contributionPoints(10L)
                    .createdAt(OffsetDateTime.now())
                    .banned(false)
                    .build();
        }

        @Test
        void login_shouldSucceed_whenEmailAndPasswordAreCorrect() {

            when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(loginRequestDto.getPassword(), mockUser.getPassword())).thenReturn(true);
            when(jwtService.generateToken(userId.toString())).thenReturn("mocked-jwt-token");

            AuthResponseDto response = authService.login(loginRequestDto);

            assertNotNull(response);
            assertEquals("mocked-jwt-token", response.getToken());
            verify(jwtService).generateToken(userId.toString());
        }

        @Test
        void login_shouldThrow_whenEmailIsNotFound() {
            when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.empty());

            assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDto));

            verifyNoInteractions(jwtService, passwordEncoder);
        }

        @Test
        void login_shouldThrow_whenPasswordIsIncorrect() {
            when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(loginRequestDto.getPassword(), "encoded-password")).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDto));

            verifyNoInteractions(jwtService);
        }

        @Test
        void login_shouldThrowLockedException_whenUserIsBanned() {
            mockUser.setBanned(true);
            when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(loginRequestDto.getPassword(), mockUser.getPassword())).thenReturn(true);

            assertThrows(LockedException.class, () -> authService.login(loginRequestDto));

            verifyNoInteractions(jwtService);
        }

        @Test
        void login_shouldThrowBadCredentials_whenUserIsBannedAndPasswordIsIncorrect() {
            mockUser.setBanned(true);
            when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(loginRequestDto.getPassword(), mockUser.getPassword())).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDto));

            verifyNoInteractions(jwtService);
        }
    }

}
