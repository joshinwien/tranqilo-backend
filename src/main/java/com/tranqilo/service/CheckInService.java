package com.tranqilo.service;

import com.tranqilo.dto.CheckInDto;
import com.tranqilo.model.CheckIn;
import com.tranqilo.model.User;
import com.tranqilo.repository.CheckInRepository;
import com.tranqilo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;

    public CheckInService(CheckInRepository checkInRepository, UserRepository userRepository) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates and saves a new check-in for a given user.
     * @param checkInDto The DTO containing the check-in data (mood, energy, notes).
     * @param username The username of the user performing the check-in.
     */
    public void createCheckIn(CheckInDto checkInDto, String username) {
        // Find the user who is creating the check-in
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found with username: " + username));

        // Create a new CheckIn entity from the DTO
        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);
        checkIn.setMood(checkInDto.getMood());
        checkIn.setEnergy(checkInDto.getEnergy());
        checkIn.setNotes(checkInDto.getNotes());
        checkIn.setCreatedAt(LocalDateTime.now()); // Set the creation timestamp

        // Save the new check-in to the database
        checkInRepository.save(checkIn);
    }
}
