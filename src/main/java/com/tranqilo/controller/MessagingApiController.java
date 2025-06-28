package com.tranqilo.controller;

import com.tranqilo.dto.ConversationDetailDto;
import com.tranqilo.dto.ConversationDto;
import com.tranqilo.dto.MessageDto;
import com.tranqilo.dto.SendMessageRequest;
import com.tranqilo.dto.StartConversationRequest;
import com.tranqilo.service.MessagingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/conversations")
    public List<ConversationDto> getConversations(Authentication authentication) {
        return messagingService.getConversationsForUser(authentication.getName());
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<ConversationDetailDto> getConversationMessages(@PathVariable Long id, Authentication authentication) {
        return messagingService.getConversationDetails(id, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/start")
    public ResponseEntity<ConversationDto> startConversation(@Valid @RequestBody StartConversationRequest request, Authentication authentication) {
        String senderUsername = authentication.getName();
        ConversationDto conversationDto = messagingService.findOrCreateConversationDto(senderUsername, request.getRecipientUsername());
        return ResponseEntity.ok(conversationDto);
    }
}