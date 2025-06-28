package com.tranqilo.service;

import com.tranqilo.dto.CheckInDto;
import com.tranqilo.model.CheckIn;
import com.tranqilo.model.User;
import com.tranqilo.repository.CheckInRepository;
import com.tranqilo.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;

    public CheckInService(CheckInRepository checkInRepository, UserRepository userRepository) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
    }

    public void createCheckIn(CheckInDto checkInDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found with username: " + username));

        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);
        checkIn.setMood(checkInDto.getMood());
        checkIn.setEnergy(checkInDto.getEnergy());
        checkIn.setNotes(checkInDto.getNotes());
        checkIn.setCreatedAt(LocalDateTime.now());

        checkInRepository.save(checkIn);
    }

    /**
     * Gets the last 7 days of check-ins for a user.
     *
     * @param username The username of the user.
     * @return A list of CheckInDto objects.
     */
    @Transactional(readOnly = true)
    public List<CheckInDto> getWeeklyCheckInSummary(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found with username: " + username));

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        List<CheckIn> checkIns = checkInRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(user.getId(), sevenDaysAgo);

        return checkIns.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets the weekly check-in summary for a specific client, but only if the
     * requesting coach is assigned to that client.
     *
     * @param clientId      The ID of the client whose data is being requested.
     * @param coachUsername The username of the coach making the request.
     * @return A list of CheckInDto objects.
     */
    @Transactional(readOnly = true)
    public List<CheckInDto> getWeeklyCheckInSummaryForClient(Long clientId, String coachUsername) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalStateException("Client not found"));

        // Security Check
        if (client.getCoach() == null || !client.getCoach().getUsername().equals(coachUsername)) {
            throw new AccessDeniedException("You are not authorized to view this client's data.");
        }

        // If authorized, reuse the existing method to get the summary
        return getWeeklyCheckInSummary(client.getUsername());
    }

    private CheckInDto convertToDto(CheckIn checkIn) {
        CheckInDto dto = new CheckInDto();
        dto.setId(checkIn.getId());
        dto.setMood(checkIn.getMood());
        dto.setEnergy(checkIn.getEnergy());
        dto.setNotes(checkIn.getNotes());
        dto.setCreatedAt(checkIn.getCreatedAt());
        dto.setUsername(checkIn.getUser().getUsername());
        return dto;
    }
}
