package com.tranqilo.controller;

import com.tranqilo.dto.CheckInDto;
import com.tranqilo.dto.UserDto;
import com.tranqilo.service.CheckInService;
import com.tranqilo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final UserService userService;
    private final CheckInService checkInService;

    public ClientController(UserService userService, CheckInService checkInService) {
        this.userService = userService;
        this.checkInService = checkInService;
    }

    /**
     * Endpoint for a coach to get the details of a specific client.
     * Includes a security check to ensure the coach is assigned to the client.
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<UserDto> getClientDetails(@PathVariable Long clientId, Authentication authentication) {
        String coachUsername = authentication.getName();
        return userService.getClientByIdForCoach(clientId, coachUsername)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint for a coach to get the 7-day check-in summary for a specific client.
     * Includes a security check.
     */
    @GetMapping("/{clientId}/check-ins/summary")
    public ResponseEntity<List<CheckInDto>> getClientCheckInSummary(@PathVariable Long clientId, Authentication authentication) {
        String coachUsername = authentication.getName();
        // The service method will handle the security check
        List<CheckInDto> summary = checkInService.getWeeklyCheckInSummaryForClient(clientId, coachUsername);
        return ResponseEntity.ok(summary);
    }
}
