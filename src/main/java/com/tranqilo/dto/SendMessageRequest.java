package com.tranqilo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotEmpty(message = "Recipient username cannot be empty.")
    private String recipientUsername;

    @NotEmpty(message = "Message content cannot be empty.")
    private String content;
}