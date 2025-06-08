package com.tranqilo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines a PasswordEncoder bean to securely hash and verify passwords.
     * BCrypt is the modern standard for password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the in-memory user details service.
     * This is a temporary setup for development. We will replace this with a
     * database-driven service later without changing the rest of the security logic.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Using the PasswordEncoder bean to hash passwords for our in-memory users.
        UserDetails coach = User.builder()
                .username("coach")
                .password(passwordEncoder().encode("coach"))
                .roles("COACH")
                .build();

        UserDetails client = User.builder()
                .username("client")
                .password(passwordEncoder().encode("client"))
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(coach, client);
    }

    /**
     * Configures the security filter chain which defines authorization rules.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Allow access to static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // Define role-based access
                        .requestMatchers("/coach/**").hasRole("COACH")
                        .requestMatchers("/client/**").hasRole("CLIENT")
                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Specify custom login page URL
                        .defaultSuccessUrl("/", true) // Redirect to home on success
                        .permitAll() // Allow everyone to see the login page
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}