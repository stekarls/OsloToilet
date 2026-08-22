package com.app.oslotoilet.auth;

import com.app.oslotoilet.enums.Role;
import com.app.oslotoilet.security.JwtService;
import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto register(RegisterRequestDto registerRequest){

        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new IllegalStateException("Email already exists: " + registerRequest.getEmail());
        }

        if(userRepository.existsByNickname(registerRequest.getNickname())){
            throw new IllegalStateException("Nickname already exists: " + registerRequest.getNickname());
        }


        User user = User.builder()
                .nickname(registerRequest.getNickname())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .contributionPoints(0L)
                .createdAt(OffsetDateTime.now())
                .banned(false)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getId().toString());

        return AuthResponseDto.builder()
                .token(token)
                .build();


    }

    public AuthResponseDto login(LoginRequestDto loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId().toString());

        return AuthResponseDto.builder()
                .token(token)
                .build();
    }
}
