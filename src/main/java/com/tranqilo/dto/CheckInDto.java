package com.tranqilo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok annotation for getters, setters, toString, etc.
public class CheckInDto {
    private Long id;
    private Integer mood;
    private Integer energy;
    // recoveryScore has been removed as it is not used in the form
    private String notes;
    private LocalDateTime createdAt;
    private String username; // To show which user this belongs to
}
