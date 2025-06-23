package com.tranqilo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "check_ins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"}) // Exclude the User object to prevent infinite loops
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer mood; // e.g., a score from 1-5

    @Column(nullable = false)
    private Integer energy; // e.g., a score from 1-5

//    @Column(name = "recovery_score", nullable = false)
//    private Integer recoveryScore; // e.g., a score from 1-10

    @Lob
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}