package com.tranqilo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddClientRequest {

    @NotEmpty(message = "Client username cannot be empty.")
    private String clientUsername;
}
