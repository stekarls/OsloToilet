package com.app.oslotoilet.services;

import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.user.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    private static final String OLD_PASSWORD_HASH = "hashed-old-password";
    private static final String NEW_PASSWORD_HASH = "hashed-new-password";

    private UUID userId;
    private User mockUser;

    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();
        mockUser = User.builder()
                .id(userId)
                .nickname("TestUser")
                .email("test@example.com")
                .password(OLD_PASSWORD_HASH)
                .role(Role.USER)
                .contributionPoints(10L)
                .createdAt(OffsetDateTime.now())
                .banned(false)
                .build();
    }

    @Nested
    class GetUsers{

        private User user1;

        private User user2;

        @BeforeEach
        void setUpThreeUsers(){
            user1 = User.builder()
                    .id(UUID.randomUUID())
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
            assertEquals(user1.getNickname(), result.get(0).getNickname());
            assertEquals(user2.getNickname(), result.get(1).getNickname());
            verify(userRepository).findAll();
        }

        @Test
        void getLeaderboard_shouldReturnMappedEntriesInRepositoryOrder(){
            when(userRepository.findAllByOrderByContributionPointsDesc()).thenReturn(List.of(user2, user1));

            List<LeaderboardEntryDto> result = userService.getLeaderboard();

            assertEquals(2, result.size());
            assertEquals(user2.getNickname(), result.get(0).getNickname());
            assertEquals(user2.getContributionPoints(), result.get(0).getContributionPoints());
            assertEquals(user1.getNickname(), result.get(1).getNickname());
            assertEquals(user1.getContributionPoints(), result.get(1).getContributionPoints());
            verify(userRepository).findAllByOrderByContributionPointsDesc();
        }


        @Test
        void getUserById_shouldReturnDto_whenUserExists(){
            when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

            UserResponseDto result = userService.getUserById(user1.getId());

            assertNotNull(result);
            assertEquals(user1.getId(), result.getId());
            assertEquals(user1.getNickname(), result.getNickname());
            assertEquals(user1.getContributionPoints(), result.getContributionPoints());
            assertEquals(user1.getCreatedAt(), result.getCreatedAt());
            assertEquals(user1.getRole(), result.getRole());
            verify(userRepository).findById(user1.getId());
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
            when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
            when(userRepository.existsByNickname(dto.getNickname())).thenReturn(false);
            when(passwordEncoder.encode(dto.getPassword())).thenReturn(NEW_PASSWORD_HASH);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User userToSave = invocation.getArgument(0);
                userToSave.setId(generatedId);
                return userToSave;
            });

            UserResponseDto response = userService.createUserAsAdmin(dto);

            assertNotNull(response);
            assertEquals(generatedId, response.getId());
            verify(userRepository).existsByEmail(dto.getEmail());
            verify(userRepository).existsByNickname(dto.getNickname());
            verify(passwordEncoder).encode(dto.getPassword());

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();
            assertEquals(dto.getNickname(), saved.getNickname());
            assertEquals(dto.getEmail(), saved.getEmail());
            assertEquals(dto.getRole(), saved.getRole());
            assertEquals(NEW_PASSWORD_HASH, saved.getPassword());
            assertNotEquals(dto.getPassword(), saved.getPassword());
            assertEquals(0L, saved.getContributionPoints());
        }

        @Test
        void createUserAsAdmin_shouldThrow_whenEmailAlreadyExists() {
            when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> userService.createUserAsAdmin(dto));

            verify(userRepository).existsByEmail(dto.getEmail());
            verify(userRepository, never()).save(any(User.class));
            verify(userRepository, never()).existsByNickname(anyString());
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void createUserAsAdmin_shouldThrow_whenNicknameAlreadyExists() {
            when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
            when(userRepository.existsByNickname(dto.getNickname())).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> userService.createUserAsAdmin(dto));

            verify(userRepository).existsByEmail(dto.getEmail());
            verify(userRepository).existsByNickname(dto.getNickname());
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
            when(userRepository.existsByNicknameAndIdNot(dto.getNickname(), userId)).thenReturn(false);

            UserResponseDto result = userService.updateNicknameById(userId, dto);

            assertEquals(dto.getNickname(), result.getNickname());
            assertEquals(dto.getNickname(), mockUser.getNickname());
        }

        @Test
        void updateNickname_shouldThrow_whenUpdatedNicknameIsTaken() {
            UserUpdateDto dto = new UserUpdateDto("updated-nickname");
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(userRepository.existsByNicknameAndIdNot(dto.getNickname(), userId)).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () -> userService.updateNicknameById(userId, dto));

            assertEquals("TestUser", mockUser.getNickname());
        }

        @Test
        void updateNickname_shouldThrow_whenUserDoesNotExist() {
            UserUpdateDto dto = new UserUpdateDto("updated-nickname");
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.updateNicknameById(userId, dto));

            verify(userRepository).findById(userId);
            verify(userRepository, never()).existsByNicknameAndIdNot(any(), any());
        }
    }

    @Nested
    class ChangePasswordTests {
        @Test
        void changePassword_shouldSucceed_whenCurrentPasswordIsCorrect() {
            ChangePasswordDto dto = new ChangePasswordDto("correct-password", "new-password");
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(dto.getCurrentPassword(), OLD_PASSWORD_HASH)).thenReturn(true);
            when(passwordEncoder.matches(dto.getNewPassword(), OLD_PASSWORD_HASH)).thenReturn(false);
            when(passwordEncoder.encode(dto.getNewPassword())).thenReturn(NEW_PASSWORD_HASH);
            when(userRepository.save(any(User.class))).thenReturn(mockUser);

            UserResponseDto response = userService.changePassword(userId, dto);

            assertEquals(NEW_PASSWORD_HASH, mockUser.getPassword());
            verify(userRepository).save(mockUser);
            assertNotNull(response);
        }


        @Test
        void changePassword_shouldThrow_whenUserDoesNotExist() {
            ChangePasswordDto dto = new ChangePasswordDto("current-password", "new-password");
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.changePassword(userId, dto));

            verify(userRepository).findById(userId);
            verifyNoInteractions(passwordEncoder);
            verify(userRepository, never()).save(any());
        }


        @Test
        void changePassword_shouldThrow_WhenCurrentPasswordIsWrong(){
            ChangePasswordDto dto = new ChangePasswordDto("wrong-password", "new-password");
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(dto.getCurrentPassword(), OLD_PASSWORD_HASH)).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> userService.changePassword(userId, dto));

            assertEquals(OLD_PASSWORD_HASH, mockUser.getPassword());
            verify(userRepository, never()).save(mockUser);
        }


        @Test
        void changePassword_shouldThrow_whenNewPasswordSameAsCurrent() {
            ChangePasswordDto dto = new ChangePasswordDto("current-password", "current-password");
            when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
            when(passwordEncoder.matches(dto.getCurrentPassword(), OLD_PASSWORD_HASH)).thenReturn(true);

            assertThrows(IllegalStateException.class, () -> userService.changePassword(userId, dto));

            assertEquals(OLD_PASSWORD_HASH, mockUser.getPassword());
            verify(passwordEncoder, never()).encode(anyString());
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

        @Test
        void banUser_shouldThrow_whenUserDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.banUser(userId));

            verify(userRepository).findById(userId);
        }

        @Test
        void unBanUser_shouldThrow_whenUserDoesNotExist() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.unBanUser(userId));

            verify(userRepository).findById(userId);
        }
    }

}
