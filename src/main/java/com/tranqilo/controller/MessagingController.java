package com.tranqilo.controller;

import com.tranqilo.service.MessagingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessagingController {

    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping("/inbox")
    public String showInbox(Model model, Authentication authentication) {
        model.addAttribute("conversations", messagingService.getConversationsForUser(authentication.getName()));
        return "inbox";
    }

    @GetMapping("/conversations/{id}")
    public String showConversation(@PathVariable("id") Long conversationId, Model model) {
        model.addAttribute("messages", messagingService.getMessagesForConversation(conversationId));
        model.addAttribute("conversationId", conversationId);
        return "conversation";
    }

    @PostMapping("/conversations/{id}/messages")
    public String sendMessage(@PathVariable("id") Long conversationId,
                              @RequestParam String content,
                              @RequestParam String recipientUsername,
                              Authentication authentication) {
        messagingService.sendMessage(authentication.getName(), recipientUsername, content);
        return "redirect:/conversations/" + conversationId;
    }
}