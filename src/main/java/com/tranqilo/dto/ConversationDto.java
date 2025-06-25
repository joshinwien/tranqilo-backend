package com.tranqilo.dto;

import lombok.Data;
import java.util.Set;

@Data
public class ConversationDto {
    private Long id;
    private Set<UserDto.ClientSummaryDto> participants; // Using the summary DTO from UserDto
    private MessageDto lastMessage;
}