package com.tranqilo.controller;

import com.tranqilo.dto.ConversationDto;
import com.tranqilo.dto.MessageDto;
import com.tranqilo.dto.SendMessageRequest;
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
    public List<MessageDto> getConversationMessages(@PathVariable Long id) {
        // Note: For a real app, you'd add a security check here
        // to ensure the logged-in user is part of this conversation.
        return messagingService.getMessagesForConversation(id);
    }
}
