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

        // Step 2: Find or create the CLIENT user
        User client = userRepository.findByUsername("client").orElseGet(() -> {
            System.out.println("Creating CLIENT user...");
            User newClient = new User("client", passwordEncoder.encode("client"), Role.CLIENT);
            return userRepository.save(newClient);
        });

        // Step 3: Check if the client is linked to the coach and link them if not.
        // This is now separate and will run every time, fixing existing data.
        if (client.getCoach() == null) {
            System.out.println("Client is not linked to a coach. Linking now...");
            client.setCoach(coach);
            userRepository.save(client);
            System.out.println("Client successfully linked to coach.");
        }
    }
}