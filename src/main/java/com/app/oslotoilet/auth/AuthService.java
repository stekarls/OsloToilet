package com.app.oslotoilet.auth;

import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto register(RegisterRequestDto request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalStateException("Email already exists: " + request.getEmail());
        }

        if(userRepository.existsByNickname(request.getNickname())){
            throw new IllegalStateException("Nickname already exists: " + request.getNickname());
        }


        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .contributionPoints(0L)
                .createdAt(OffsetDateTime.now())
                .build();

        userRepository.save(user);

        return AuthResponseDto.builder()
                .token(null) //TODO: Implement JWT token generation
                .build();


    }


}
