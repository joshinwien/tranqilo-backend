package com.tranqilo.service;

import com.tranqilo.dto.RegistrationDto;
import com.tranqilo.model.Role;
import com.tranqilo.model.User;
import com.tranqilo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegistrationDto registrationDto) {
        // Check if username already exists
        if (userRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }

        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        // Always encode the password before saving!
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRole(registrationDto.getRole());

        userRepository.save(newUser);
    }

    public void assignClientToCoach(String clientUsername, String coachUsername) {
        User coach = userRepository.findByUsername(coachUsername)
                .orElseThrow(() -> new IllegalStateException("Coach not found"));
        User client = userRepository.findByUsername(clientUsername)
                .orElseThrow(() -> new IllegalStateException("Client not found"));

        if (client.getRole() != Role.CLIENT) {
            throw new IllegalStateException("Cannot add a user who is not a client.");
        }

        if (client.getCoach() != null) {
            throw new IllegalStateException("Client is already assigned to another coach.");
        }

        client.setCoach(coach);
        userRepository.save(client);
    }

    public void removeClientFromCoach(String clientUsername, String coachUsername) {
        User coach = userRepository.findByUsername(coachUsername)
                .orElseThrow(() -> new IllegalStateException("Coach not found"));
        User client = userRepository.findByUsername(clientUsername)
                .orElseThrow(() -> new IllegalStateException("Client not found"));

        // Security check: ensure the client belongs to this coach
        if (client.getCoach() == null || !client.getCoach().getId().equals(coach.getId())) {
            throw new IllegalStateException("This client is not assigned to you.");
        }

        client.setCoach(null); // This severs the relationship
        userRepository.save(client);
    }
}