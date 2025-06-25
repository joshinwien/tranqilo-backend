package com.tranqilo.dto;

import com.tranqilo.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Setter
@Getter
public class RegistrationDto {

    // Getters and Setters
    @NotEmpty(message = "Username cannot be empty.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    @NotEmpty(message = "Password cannot be empty.")
    @Size(min = 3, message = "Password must be at least 3 characters long.")
    private String password;

    @NotEmpty(message = "Please confirm your password.")
    private String confirmPassword;

    @NotNull(message = "You must select a role.")
    private Role role;

    @NotEmpty(message = "First name cannot be empty.")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty.")
    private String lastName;

    @NotEmpty(message = "Email cannot be empty.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotNull(message = "Birth date cannot be empty.")
    @Past(message = "Birth date must be in the past.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

}