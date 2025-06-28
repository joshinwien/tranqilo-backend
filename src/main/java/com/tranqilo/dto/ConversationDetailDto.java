package com.tranqilo.dto;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class ConversationDetailDto {
    private Long id;
    private Set<UserDto.ClientSummaryDto> participants;
    private List<MessageDto> messages;
}
