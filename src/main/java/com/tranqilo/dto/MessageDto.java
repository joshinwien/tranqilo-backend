package com.tranqilo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long senderId;
    private String senderUsername;
}