package com.tranqilo.config;

import com.tranqilo.model.Role;
import com.tranqilo.model.User;
import com.tranqilo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // --- COACH ---
        User coach = userRepository.findByUsername("coach").orElseGet(() -> {
            // Use the no-argument constructor and setters instead
            User newCoach = new User();
            newCoach.setUsername("coach");
            newCoach.setPassword(passwordEncoder.encode("coach"));
            newCoach.setRole(Role.COACH);
            return newCoach;
        });
        // If the user was just created or existed without details, update them.
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
            // Use the no-argument constructor and setters instead
            User newClient = new User();
            newClient.setUsername("client");
            newClient.setPassword(passwordEncoder.encode("client"));
            newClient.setRole(Role.CLIENT);
            return newClient;
        });
        // If the user was just created or existed without details, update them.
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
            // Use the no-argument constructor and setters instead
            User newClient2 = new User();
            newClient2.setUsername("client2");
            newClient2.setPassword(passwordEncoder.encode("client2"));
            newClient2.setRole(Role.CLIENT);
            return newClient2;
        });
        // If the user was just created or existed without details, update them.
        if (client2.getFirstName() == null) {
            System.out.println("Updating details for CLIENT 'client2'...");
            client2.setFirstName("Cameron");
            client2.setLastName("Cliente");
            client2.setEmail("cameron.cliente@tranqilo.com");
            client2.setBirthDate(LocalDate.of(1998, 11, 30));
            client2.setProfilePictureUrl("/user-uploads/profile-pictures/client2.jpg");
            userRepository.save(client2);
        }

        // --- LINKING LOGIC (runs last to ensure both objects are up-to-date) ---
        if (client.getCoach() == null) {
            System.out.println("Linking 'client' to 'coach'...");
            client.setCoach(coach);
            userRepository.save(client);
        }
    }
}