package com.app.oslotoilet.user;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/contribution")
    public List<UserResponseDto> sortByContributionPoints(){
        return userService.sortByContributionPoints();
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id){
        UserResponseDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createNewUser(@Valid @RequestBody UserRequestDto user){
        return new ResponseEntity<>(userService.createNewUser(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable UUID id, @Valid @RequestBody UserUpdateDto userUpdateDto){
        UserResponseDto response = userService.updateUserById(id, userUpdateDto);
        return ResponseEntity.ok(response);
    }


}
