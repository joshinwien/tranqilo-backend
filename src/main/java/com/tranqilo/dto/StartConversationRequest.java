package com.tranqilo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class StartConversationRequest {
    @NotEmpty(message = "Recipient username cannot be empty.")
    private String recipientUsername;
}
