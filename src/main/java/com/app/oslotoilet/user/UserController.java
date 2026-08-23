package com.app.oslotoilet.user;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/contribution")
    public ResponseEntity<List<UserResponseDto>> sortByContributionPoints(){
        return ResponseEntity.ok(userService.sortByContributionPoints());
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id){
        UserResponseDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody AdminCreateUserDto user){
        return new ResponseEntity<>(userService.createUserAsAdmin(user), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateNicknameById(@PathVariable UUID id, @Valid @RequestBody UserUpdateDto userUpdateDto){
        UserResponseDto response = userService.updateNicknameById(id, userUpdateDto);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    @PatchMapping("/{id}/password")
    public ResponseEntity<UserResponseDto> changePassword(@PathVariable UUID id, @RequestBody @Valid ChangePasswordDto changePasswordDto){
        return ResponseEntity.ok(userService.changePassword(id, changePasswordDto));
    }

    @PatchMapping("/{userId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> banUser(@PathVariable UUID userId) {
        userService.banUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
