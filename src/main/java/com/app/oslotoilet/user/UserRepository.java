package com.app.oslotoilet.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByNicknameAndIdNot(String nickname, UUID id);
    List<User> findAllByOrderByContributionPointsDesc();
}
