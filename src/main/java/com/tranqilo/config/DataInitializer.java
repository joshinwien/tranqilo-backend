package com.tranqilo.config;

import com.tranqilo.model.Role;
import com.tranqilo.model.User;
import com.tranqilo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create COACH user if not exists
        if (userRepository.findByUsername("coach").isEmpty()) {
            User coach = new User();
            coach.setUsername("coach");
            // IMPORTANT: Always encode passwords before saving
            coach.setPassword(passwordEncoder.encode("coach"));
            coach.setRole(Role.COACH);
            userRepository.save(coach);
            System.out.println("Created COACH user");
        }

        // Create CLIENT user if not exists
        if (userRepository.findByUsername("client").isEmpty()) {
            User client = new User();
            client.setUsername("client");
            client.setPassword(passwordEncoder.encode("client"));
            client.setRole(Role.CLIENT);
            userRepository.save(client);
            System.out.println("Created CLIENT user");
        }
    }
}