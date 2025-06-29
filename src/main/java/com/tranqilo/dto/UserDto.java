package com.tranqilo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tranqilo.model.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // This will hide null fields in the JSON output
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String profilePictureUrl;

    // For Coaches
    private Set<ClientSummaryDto> clients;

    // For Clients
    private CoachSummaryDto coach;

    @Data
    public static class CoachSummaryDto {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
    }

    @Data
    public static class ClientSummaryDto {
        private Long id;
        private String username;
        private String firstName;
        private String lastName;
    }
}