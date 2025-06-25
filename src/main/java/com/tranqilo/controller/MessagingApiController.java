package com.tranqilo.controller;

import com.tranqilo.dto.SendMessageRequest;
import com.tranqilo.service.MessagingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messaging")
public class MessagingApiController {

    private final MessagingService messagingService;

    public MessagingApiController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@Valid @RequestBody SendMessageRequest request, Authentication authentication) {
        try {
            messagingService.sendMessage(
                    authentication.getName(),
                    request.getRecipientUsername(),
                    request.getContent()
            );
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}