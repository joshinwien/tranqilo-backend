package com.tranqilo.service;

import com.tranqilo.dto.ConversationDto;
import com.tranqilo.dto.MessageDto;
import com.tranqilo.dto.UserDto;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.tranqilo.dto.ConversationDto;
import com.tranqilo.dto.MessageDto;
import com.tranqilo.dto.UserDto;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<ConversationDto> getConversationsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return conversationRepository.findByParticipantId(user.getId()).stream()
                .map(conversation -> convertToConversationDto(conversation, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesForConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .map(conversation -> conversation.getMessages().stream()
                        .map(this::convertToMessageDto)
                        .sorted(Comparator.comparing(MessageDto::getCreatedAt))
                        .collect(Collectors.toList()))
                .orElse(List.of()); // Return empty list if conversation not found
    }

    private ConversationDto convertToConversationDto(Conversation conversation, User currentUser) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());

        // Set participants (excluding the current user)
        dto.setParticipants(
                conversation.getParticipants().stream()
                        .filter(participant -> !participant.getId().equals(currentUser.getId()))
                        .map(participant -> {
                            UserDto.ClientSummaryDto summary = new UserDto.ClientSummaryDto();
                            summary.setId(participant.getId());
                            summary.setUsername(participant.getUsername());
                            summary.setFirstName(participant.getFirstName());
                            summary.setLastName(participant.getLastName());
                            return summary;
                        })
                        .collect(Collectors.toSet())
        );

        // Find and set the last message
        conversation.getMessages().stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .ifPresent(lastMessage -> dto.setLastMessage(convertToMessageDto(lastMessage)));

        return dto;
    }

    private MessageDto convertToMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderUsername(message.getSender().getUsername());
        return dto;
    }
}