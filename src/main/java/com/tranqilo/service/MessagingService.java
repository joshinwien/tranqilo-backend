package com.tranqilo.service;

import com.tranqilo.model.Conversation;
import com.tranqilo.model.Message;
import com.tranqilo.model.User;
import com.tranqilo.repository.ConversationRepository;
import com.tranqilo.repository.MessageRepository;
import com.tranqilo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class MessagingService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public MessagingService(MessageRepository messageRepository, ConversationRepository conversationRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    public void sendMessage(String senderUsername, String recipientUsername, String content) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new IllegalStateException("Sender not found"));
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new IllegalStateException("Recipient not found"));

        // Find existing conversation or create a new one
        Conversation conversation = conversationRepository.findByParticipants(sender, recipient)
                .orElseGet(() -> createNewConversation(sender, recipient));

        Message message = new Message();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());

        messageRepository.save(message);
    }

    private Conversation createNewConversation(User user1, User user2) {
        Conversation conversation = new Conversation();
        Set<User> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);
        conversation.setParticipants(participants);
        return conversationRepository.save(conversation);
    }
}