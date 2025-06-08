package com.tranqilo.config;

import com.tranqilo.model.Role;
import com.tranqilo.model.User;
import com.tranqilo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        // Step 1: Find or create the COACH user
        User coach = userRepository.findByUsername("coach").orElseGet(() -> {
            System.out.println("Creating COACH user...");
            User newCoach = new User("coach", passwordEncoder.encode("coach"), Role.COACH);
            return userRepository.save(newCoach);
        });

        // Step 2: Find or create the first CLIENT user
        User client = userRepository.findByUsername("client").orElseGet(() -> {
            System.out.println("Creating CLIENT user 'client'...");
            User newClient = new User("client", passwordEncoder.encode("client"), Role.CLIENT);
            return userRepository.save(newClient);
        });

        // Step 3: Ensure the first CLIENT is always linked to the COACH
        if (client.getCoach() == null) {
            System.out.println("Linking 'client' to 'coach'...");
            client.setCoach(coach);
            userRepository.save(client);
        }

        // --- NEW SECTION ---
        // Step 4: Find or create the second, unassigned CLIENT user ('client2')
        userRepository.findByUsername("client2").orElseGet(() -> {
            System.out.println("Creating unassigned CLIENT user 'client2'...");
            User client2 = new User("client2", passwordEncoder.encode("client2"), Role.CLIENT);
            // We do NOT set a coach for client2. It will remain unassigned.
            return userRepository.save(client2);
        });
    }
}