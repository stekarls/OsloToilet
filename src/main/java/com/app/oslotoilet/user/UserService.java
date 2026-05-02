package com.app.oslotoilet.user;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(UUID userId){
        return userRepository.findById(userId).orElse(null);
    }

    public User createNewUser(User user){
        return userRepository.save(user);
    }

    public void deleteUserById(UUID userId){
        userRepository.deleteById(userId);
    }

    public User updateUserById(UUID userId){
        return null;
    }

}
