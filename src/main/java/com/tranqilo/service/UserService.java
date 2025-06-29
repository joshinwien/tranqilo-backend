package com.tranqilo.service;

import com.tranqilo.dto.RegistrationDto;
import com.tranqilo.dto.UserDto;
import com.tranqilo.model.Role;
import com.tranqilo.model.User;
import com.tranqilo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        // Check if email already exists
        // (You should create a findByEmail method in your UserRepository for this)
        // if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
        //     throw new IllegalStateException("Email already in use");
        // }

        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }

        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        // Always encode the password before saving!
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRole(registrationDto.getRole());
        newUser.setFirstName(registrationDto.getFirstName());
        newUser.setLastName(registrationDto.getLastName());
        newUser.setEmail(registrationDto.getEmail());
        newUser.setBirthDate(registrationDto.getBirthDate());

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

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsersAsDto() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByIdAsDto(Long id) {
        return userRepository.findById(id).map(this::convertToDto);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());

        if (user.getRole() == Role.COACH) {
            dto.setClients(user.getClients().stream().map(this::convertClientToSummaryDto).collect(Collectors.toSet()));
        }

        if (user.getRole() == Role.CLIENT && user.getCoach() != null) {
            dto.setCoach(convertCoachToSummaryDto(user.getCoach()));
        }

        return dto;
    }

    private UserDto.ClientSummaryDto convertClientToSummaryDto(User client) {
        UserDto.ClientSummaryDto summaryDto = new UserDto.ClientSummaryDto();
        summaryDto.setId(client.getId());
        summaryDto.setUsername(client.getUsername());
        summaryDto.setFirstName(client.getFirstName());
        summaryDto.setLastName(client.getLastName());
        return summaryDto;
    }

    private UserDto.CoachSummaryDto convertCoachToSummaryDto(User coach) {
        UserDto.CoachSummaryDto summaryDto = new UserDto.CoachSummaryDto();
        summaryDto.setId(coach.getId());
        summaryDto.setUsername(coach.getUsername());
        summaryDto.setFirstName(coach.getFirstName());
        summaryDto.setLastName(coach.getLastName());
        return summaryDto;
    }
}