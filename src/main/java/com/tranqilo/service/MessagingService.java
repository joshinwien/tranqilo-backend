package com.tranqilo.service;

import com.tranqilo.dto.ConversationDetailDto;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Transactional
    public ConversationDto findOrCreateConversationDto(String username1, String username2) {
        User user1 = userRepository.findByUsername(username1)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username1));
        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username2));

        Conversation conversation = conversationRepository.findByParticipants(user1, user2)
                .orElseGet(() -> createNewConversation(user1, user2));

        return convertToConversationDto(conversation, user1);
    }

    public void sendMessage(String senderUsername, String recipientUsername, String content) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new IllegalStateException("Sender not found"));
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new IllegalStateException("Recipient not found"));
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
                .orElse(List.of());
    }

    /**
     * Gets the full details for a single conversation, including participants and messages.
     * Performs a check to ensure the requesting user is part of the conversation.
     * @param conversationId The ID of the conversation.
     * @param username The username of the user making the request.
     * @return An Optional containing the ConversationDetailDto if found and authorized.
     */
    @Transactional(readOnly = true)
    public Optional<ConversationDetailDto> getConversationDetails(Long conversationId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        return conversationRepository.findById(conversationId)
                // Security check: ensure the current user is a participant
                .filter(conversation -> conversation.getParticipants().stream()
                        .anyMatch(p -> p.getId().equals(currentUser.getId())))
                .map(conversation -> convertToConversationDetailDto(conversation, currentUser));
    }

    private ConversationDto convertToConversationDto(Conversation conversation, User currentUser) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setParticipants(
                conversation.getParticipants().stream()
                        .filter(participant -> !participant.getId().equals(currentUser.getId()))
                        .map(this::convertUserToClientSummaryDto)
                        .collect(Collectors.toSet())
        );
        conversation.getMessages().stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .ifPresent(lastMessage -> dto.setLastMessage(convertToMessageDto(lastMessage)));
        return dto;
    }

    private ConversationDetailDto convertToConversationDetailDto(Conversation conversation, User currentUser) {
        ConversationDetailDto dto = new ConversationDetailDto();
        dto.setId(conversation.getId());

        // Set the participants, including the current user in this case for context
        dto.setParticipants(
                conversation.getParticipants().stream()
                        .map(this::convertUserToClientSummaryDto)
                        .collect(Collectors.toSet())
        );

        // Set the messages, sorted by creation date
        dto.setMessages(
                conversation.getMessages().stream()
                        .map(this::convertToMessageDto)
                        .sorted(Comparator.comparing(MessageDto::getCreatedAt))
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private UserDto.ClientSummaryDto convertUserToClientSummaryDto(User user) {
        UserDto.ClientSummaryDto summary = new UserDto.ClientSummaryDto();
        summary.setId(user.getId());
        summary.setUsername(user.getUsername());
        summary.setFirstName(user.getFirstName());
        summary.setLastName(user.getLastName());
        return summary;
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