package com.tranqilo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "checkIn"}) // Exclude relationships to prevent infinite loops
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // e.g., "Running", "Strength Training", "Yoga"
    @Column(name = "activity_type", nullable = false)
    private String type;

    @Column(name = "activity_date", nullable = false)
    private LocalDateTime activityDate;

    // Optional link to a check-in
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_in_id")
    private CheckIn checkIn;
}