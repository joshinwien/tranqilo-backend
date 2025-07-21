package com.tranqilo.config;

import com.tranqilo.model.CheckIn;
import com.tranqilo.model.Role;
import com.tranqilo.model.User;
import com.tranqilo.repository.CheckInRepository;
import com.tranqilo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CheckInRepository checkInRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, CheckInRepository checkInRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.checkInRepository = checkInRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // --- COACH ---
        User coach = userRepository.findByUsername("coach").orElseGet(() -> {
            User newCoach = new User();
            newCoach.setUsername("coach");
            newCoach.setPassword(passwordEncoder.encode("coach"));
            newCoach.setRole(Role.COACH);
            return newCoach;
        });
        if (coach.getFirstName() == null) {
            System.out.println("Updating details for COACH user...");
            coach.setFirstName("Conor");
            coach.setLastName("Coach");
            coach.setEmail("conor.coach@tranqilo.com");
            coach.setBirthDate(LocalDate.of(1985, 5, 15));
            coach.setProfilePictureUrl("/user-uploads/profile-pictures/coach.jpg");
            coach = userRepository.save(coach);
        }

        // --- CLIENT ---
        User client = userRepository.findByUsername("client").orElseGet(() -> {
            User newClient = new User();
            newClient.setUsername("client");
            newClient.setPassword(passwordEncoder.encode("client"));
            newClient.setRole(Role.CLIENT);
            return newClient;
        });
        if (client.getFirstName() == null) {
            System.out.println("Updating details for CLIENT 'client'...");
            client.setFirstName("Charles");
            client.setLastName("Client");
            client.setEmail("charles.client@tranqilo.com");
            client.setBirthDate(LocalDate.of(1992, 8, 20));
            client.setProfilePictureUrl("/user-uploads/profile-pictures/client.jpg");
            client = userRepository.save(client);
        }

        // --- CLIENT 2 ---
        User client2 = userRepository.findByUsername("client2").orElseGet(() -> {
            User newClient2 = new User();
            newClient2.setUsername("client2");
            newClient2.setPassword(passwordEncoder.encode("client2"));
            newClient2.setRole(Role.CLIENT);
            return newClient2;
        });
        if (client2.getFirstName() == null) {
            System.out.println("Updating details for CLIENT 'client2'...");
            client2.setFirstName("Cameron");
            client2.setLastName("Cliente");
            client2.setEmail("cameron.cliente@tranqilo.com");
            client2.setBirthDate(LocalDate.of(1998, 11, 30));
            client2.setProfilePictureUrl("/user-uploads/profile-pictures/client2.jpg");
            client2 = userRepository.save(client2);
        }

        // --- LINKING LOGIC ---
        if (client.getCoach() == null) {
            System.out.println("Linking 'client' to 'coach'...");
            client.setCoach(coach);
            userRepository.save(client);
        }

        // --- HISTORICAL CHECK-IN DATA ---
        createHistoricalCheckInsForUser(client);
        createHistoricalCheckInsForUser(client2);
    }

    /**
     * Checks the last 7 days and creates historical check-in data for any missing days.
     * This method is idempotent and will not create duplicate data on restart.
     *
     * @param user The user for whom to create check-ins.
     */
    private void createHistoricalCheckInsForUser(User user) {
        // 1. Get all check-ins from the last 7 days.
        List<CheckIn> recentCheckIns = checkInRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(
                user.getId(),
                LocalDateTime.now().minusDays(7)
        );

        // 2. Extract just the dates of these check-ins into a Set for efficient lookup.
        Set<LocalDate> existingDates = recentCheckIns.stream()
                .map(checkIn -> checkIn.getCreatedAt().toLocalDate())
                .collect(Collectors.toSet());

        boolean didCreateData = false;
        // 3. Loop through the last 7 days to see which days are missing.
        for (int i = 0; i < 7; i++) {
            LocalDate dateToCheck = LocalDate.now().minusDays(i);

            // 4. If a check-in doesn't exist for this day, create one.
            if (!existingDates.contains(dateToCheck)) {
                if (!didCreateData) {
                    System.out.println("Backfilling historical check-in data for user: " + user.getUsername());
                    didCreateData = true;
                }

                CheckIn checkIn = new CheckIn();
                checkIn.setUser(user);
                checkIn.setMood(ThreadLocalRandom.current().nextInt(3, 10)); // Mood from 3 to 9
                checkIn.setEnergy(ThreadLocalRandom.current().nextInt(3, 10)); // Energy from 3 to 9
                checkIn.setNotes("Auto-generated historical check-in.");
                // Set the timestamp to sometime during that day, e.g., 10:00 AM
                checkIn.setCreatedAt(dateToCheck.atTime(10, 0));
                checkInRepository.save(checkIn);
            }
        }
    }
}
