package com.app.oslotoilet.repositories;

import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .nickname("Bobby")
                .email("bob@example.com")
                .password("encoded-password-1")
                .contributionPoints(50L)
                .createdAt(OffsetDateTime.now())
                .role(Role.USER)
                .banned(false)
                .build();

        user2 = User.builder()
                .nickname("Alice")
                .email("alice@example.com")
                .password("encoded-password-1")
                .contributionPoints(100L)
                .createdAt(OffsetDateTime.now())
                .role(Role.USER)
                .banned(false)
                .build();

        user1 = testEntityManager.persistAndFlush(user1);
        user2 = testEntityManager.persistAndFlush(user2);
    }


    @Test
    void existsByNicknameAndIdNot_shouldReturnFalse_forOwnNickname() {
        assertThat(userRepository.existsByNicknameAndIdNot("Alice", user2.getId())).isFalse();
    }

    @Test
    void existsByNicknameAndIdNot_shouldReturnTrue_whenAnotherHasNickname() {
        assertThat(userRepository.existsByNicknameAndIdNot("Alice", user1.getId())).isTrue();
    }

    @Test
    void existsByNickname_shouldReturnTrue_whenAUserWithNicknameExists() {
        assertTrue(userRepository.existsByNickname("Bobby"));
    }

    @Test
    void existsByNickname_shouldReturnFalse_whenNicknameDoesNotExist() {
        assertFalse(userRepository.existsByNickname("Charlie"));
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        assertTrue(userRepository.existsByEmail("alice@example.com"));
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        assertFalse(userRepository.existsByEmail("charlie@example.com"));
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        assertThat(userRepository.findByEmail("charlie@example.com")).isEmpty();
    }

    @Test
    void findAllByOrderByContributionPointsDesc() {
        List<User> result = userRepository.findAllByOrderByContributionPointsDesc();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNickname()).isEqualTo("Alice");
        assertThat(result.get(1).getNickname()).isEqualTo("Bobby");
    }
}
