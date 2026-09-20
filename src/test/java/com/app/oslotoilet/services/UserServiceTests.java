package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.user.*;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.List;
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
    class GetUsers{

        private UUID userId;
        private User user1;

        private User user2;

        @BeforeEach
        void setUpThreeUsers(){
            userId = UUID.randomUUID();
            user1 = User.builder()
                    .id(userId)
                    .nickname("user1")
                    .role(Role.USER)
                    .contributionPoints(19L)
                    .createdAt(OffsetDateTime.now())
                    .build();

            user2 = User.builder()
                    .id(UUID.randomUUID())
                    .nickname("user2")
                    .role(Role.USER)
                    .contributionPoints(140L)
                    .createdAt(OffsetDateTime.now())
                    .build();

        }

        @Test
        void getAllUsers_shouldReturnMappedDtoList(){
            when(userRepository.findAll()).thenReturn(List.of(user1, user2));

            List<UserResponseDto> result = userService.getAllUsers();

            assertEquals(2, result.size());
            assertEquals("user1", result.get(0).getNickname());
            verify(userRepository).findAll();
        }

        @Test
        void sortByContributionPoints_shouldReturnMappedDtoList(){
            when(userRepository.findAllByOrderByContributionPointsDesc()).thenReturn(List.of(user1, user2));

            List<UserResponseDto> result = userService.sortByContributionPoints();

            assertEquals(2, result.size());
            assertEquals("user1", result.get(0).getNickname());
            verify(userRepository).findAllByOrderByContributionPointsDesc();
        }


        @Test
        void getUserById_shouldReturnDto_whenUserExists(){
            when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

            UserResponseDto result = userService.getUserById(userId);

            assertNotNull(result);
            assertEquals(userId, result.getId());
            verify(userRepository).findById(userId);
        }

        @Test
        void getUserById_shouldThrow_whenUserDoesNotExists(){
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));

            verify(userRepository).findById(userId);
        }

    }

    @Nested
    class AdminUserCreation {

        AdminCreateUserDto dto = new AdminCreateUserDto(
                "testUser",
                "test@example.com",
                "rawPassword123",
                Role.USER);

        @Test
        void createUserAsAdmin_shouldSucceed_whenEmailAndNicknameDoesNotExist() {
            UUID generatedId = UUID.randomUUID();
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(userRepository.existsByNickname("testUser")).thenReturn(false);
            when(passwordEncoder.encode("rawPassword123")).thenReturn("hashed-new-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                userToSave.setId(generatedId);
                return userToSave;
            });

            UserResponseDto response = userService.createUserAsAdmin(dto);

            assertNotNull(response);
            verify(userRepository).existsByEmail("test@example.com");
            verify(userRepository).existsByNickname("testUser");
            verify(passwordEncoder).encode("rawPassword123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        void createUserAsAdmin_shouldThrow_whenEmailDoesNotExist() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> userService.createUserAsAdmin(dto));

            verify(userRepository).existsByEmail("test@example.com");
            verify(userRepository, never()).save(any(User.class));
            verify(userRepository, never()).existsByNickname(anyString());
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void createUserAsAdmin_shouldThrow_whenNicknameDoesNotExist() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(userRepository.existsByNickname("testUser")).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> userService.createUserAsAdmin(dto));

            verify(userRepository).existsByEmail("test@example.com");
            verify(userRepository).existsByNickname("testUser");
            verify(userRepository, never()).save(any(User.class));
            verifyNoInteractions(passwordEncoder);
        }

    }

    @Nested
    class DeleteUserTests {

        @Test
        void deleteUserById_shouldSucceed_providedIdExists() {
            when(userRepository.existsById(userId)).thenReturn(true);

            userService.deleteUserById(userId);

            verify(userRepository).existsById(userId);
            verify(userRepository).deleteById(userId);
        }

        @Test
        void deleteByUserId_shouldThrow_whenUserIdDoesNotExist() {
            when(userRepository.existsById(userId)).thenReturn(false);

            assertThrows(EntityNotFoundException.class ,() -> userService.deleteUserById(userId));

            verify(userRepository).existsById(userId);
            verify(userRepository, never()).deleteById(userId);
        }
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
        void changePassword_shouldThrow_WhenCurrentPasswordIsWrong(){
            ChangePasswordDto dto = new ChangePasswordDto("wrong-password", "new-password");
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches("wrong-password", "hashed-old-password")).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> userService.changePassword(userId, dto));

            verify(userRepository, never()).save(mockUser);
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
