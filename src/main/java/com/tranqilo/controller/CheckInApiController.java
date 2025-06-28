package com.tranqilo.controller;

import com.tranqilo.dto.CheckInDto;
import com.tranqilo.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/check-ins")
public class CheckInApiController {

    private final CheckInService checkInService;

    public CheckInApiController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public ResponseEntity<Void> createCheckIn(@Valid @RequestBody CheckInDto checkInDto, Authentication authentication) {
        String username = authentication.getName();
        checkInService.createCheckIn(checkInDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * API endpoint for getting the last 7 days of check-in data for the logged-in user.
     */
    @GetMapping("/summary")
    public ResponseEntity<List<CheckInDto>> getCheckInSummary(Authentication authentication) {
        String username = authentication.getName();
        List<CheckInDto> summary = checkInService.getWeeklyCheckInSummary(username);
        return ResponseEntity.ok(summary);
    }
}
