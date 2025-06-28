package com.tranqilo.controller;

import com.tranqilo.dto.AddClientRequest;
import com.tranqilo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coach")
public class CoachApiController {

    private final UserService userService;

    public CoachApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/clients")
    public ResponseEntity<Void> addClient(@Valid @RequestBody AddClientRequest request, Authentication authentication) {
        String coachUsername = authentication.getName();
        userService.assignClientToCoach(request.getClientUsername(), coachUsername);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clients/{clientUsername}")
    public ResponseEntity<Void> removeClient(@PathVariable String clientUsername, Authentication authentication) {
        String coachUsername = authentication.getName();
        userService.removeClientFromCoach(clientUsername, coachUsername);
        return ResponseEntity.noContent().build();
    }
}
