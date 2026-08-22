package com.app.oslotoilet.security;


import com.app.oslotoilet.user.User;
import com.app.oslotoilet.user.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) {
        User user = userRepository.findByNickname(email).orElseThrow(() -> new UsernameNotFoundException("User not found with nickname: " + email));
        return new SecurityUser(user);
    }

    public UserDetails loadUserById(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return new SecurityUser(user);
    }
}
