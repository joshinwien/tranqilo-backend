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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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

    @Transactional
    public Conversation findOrCreateConversation(String username1, String username2) {
        User user1 = userRepository.findByUsername(username1)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username1));
        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username2));

        return conversationRepository.findByParticipants(user1, user2)
                .orElseGet(() -> createNewConversation(user1, user2));
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
                        .sorted(Comparator.comparing(MessageDto::getCreatedAt)) // Ensure messages are in order
                        .collect(Collectors.toList()))
                .orElse(List.of()); // Return an empty list if conversation isn't found
    }

    // Helper method to convert a Conversation entity to a DTO
    private ConversationDto convertToConversationDto(Conversation conversation, User currentUser) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());

        // Find the other participant to display their name
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

        // Find and set the last message for a preview in the inbox
        conversation.getMessages().stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .ifPresent(lastMessage -> dto.setLastMessage(convertToMessageDto(lastMessage)));

        return dto;
    }

    // Helper method to convert a Message entity to a DTO
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
