package com.tranqilo.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ConversationDto {
    private Long id;
    private Set<UserDto.ClientSummaryDto> participants;
    private MessageDto lastMessage;
}