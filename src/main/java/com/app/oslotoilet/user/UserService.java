package com.app.oslotoilet.user;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDto> getAllUsers(){
        return userRepository.findAll().stream().map(this::mapToResponseDto).toList();
    }

    public List<UserResponseDto> sortByContributionPoints(){
        return userRepository.findAllByOrderByContributionPointsDesc().stream().map(this::mapToResponseDto).toList();
    }

    public UserResponseDto getUserById(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        return mapToResponseDto(user);
    }

    public UserResponseDto createUserAsAdmin(AdminCreateUserDto requestDto){
        if (userRepository.existsByEmail(requestDto.getEmail())){
            throw new IllegalStateException("User with email " + requestDto.getEmail() + " already exists.");
        }
        if (userRepository.existsByNickname(requestDto.getNickname())){
            throw new IllegalStateException("User with nickname " + requestDto.getNickname() + " already exists.");
        }

        User user = User.builder()
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(requestDto.getRole())
                .contributionPoints(0L)
                .createdAt(OffsetDateTime.now())
                .build();
        return mapToResponseDto(userRepository.save(user));
    }

    public void deleteUserById(UUID userId){
        if (!userRepository.existsById(userId)){
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public UserResponseDto updateNicknameById(UUID userId, UserUpdateDto updateDto){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (userRepository.existsByNicknameAndIdNot(updateDto.getNickname(), userId)) {
            throw new IllegalArgumentException("The nickname '" + updateDto.getNickname() + "' is already taken");
        }
        user.setNickname(updateDto.getNickname());
        return mapToResponseDto(user);
    }

    public void banUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setBanned(true);
    }

    private UserResponseDto mapToResponseDto(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .contributionPoints(user.getContributionPoints())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
