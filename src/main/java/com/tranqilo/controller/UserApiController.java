package com.tranqilo.controller;

import com.tranqilo.dto.ProfileUpdateDto;
import com.tranqilo.dto.UserDto;
import com.tranqilo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsersAsDto();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserByIdAsDto(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyDetails(Authentication authentication) {
        return userService.getUserByUsernameAsDto(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserDto> updateMyProfile(@Valid @RequestBody ProfileUpdateDto profileUpdateDto, Authentication authentication) {
        String username = authentication.getName();
        UserDto updatedUser = userService.updateUserProfile(username, profileUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/unassigned")
    public List<UserDto> getUnassignedClients() {
        return userService.getUnassignedClients();
    }
}
