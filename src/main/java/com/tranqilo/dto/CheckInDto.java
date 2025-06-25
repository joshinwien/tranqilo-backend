package com.tranqilo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok annotation for getters, setters, toString, etc.
public class CheckInDto {
    private Long id;
    private Integer mood;
    private Integer energy;
    private Integer recoveryScore;
    private String notes;
    private LocalDateTime createdAt;
    private String username; // To show which user this belongs to
}