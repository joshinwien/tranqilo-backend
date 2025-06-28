package com.tranqilo.controller;

import com.tranqilo.dto.CheckInDto;
import com.tranqilo.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/check-ins")
public class CheckInApiController {

    private final CheckInService checkInService;

    public CheckInApiController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    /**
     * API endpoint for creating a new check-in.
     * Accessible only by authenticated users.
     */
    @PostMapping
    public ResponseEntity<Void> createCheckIn(@Valid @RequestBody CheckInDto checkInDto, Authentication authentication) {
        // Get the username of the currently logged-in user from the security context
        String username = authentication.getName();

        // Call the service to create the check-in
        checkInService.createCheckIn(checkInDto, username);

        // Return a 201 Created status on success
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
