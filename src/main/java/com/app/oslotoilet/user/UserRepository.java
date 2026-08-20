package com.app.oslotoilet.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByNicknameAndIdNot(String nickname, UUID id);

    List<User> findAllByOrderByContributionPointsDesc();

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
