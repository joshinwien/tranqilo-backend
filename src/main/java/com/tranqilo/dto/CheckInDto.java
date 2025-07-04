package com.tranqilo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckInDto {
    private Long id;
    private Integer mood;
    private Integer energy;
    private String notes;
    private LocalDateTime createdAt;
    private String username;
}
