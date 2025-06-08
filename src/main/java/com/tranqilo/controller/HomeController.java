package com.tranqilo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Shows the login page.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Redirects users to their specific dashboard after login,
     * or shows a generic home page.
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if ("ROLE_COACH".equals(auth.getAuthority())) {
                    return "redirect:/coach/dashboard";
                }
                if ("ROLE_CLIENT".equals(auth.getAuthority())) {
                    return "redirect:/client/dashboard";
                }
            }
        }
        // Fallback for any other case or if no specific role dashboard is needed
        return "home";
    }

    @GetMapping("/coach/dashboard")
    public String coachDashboard() {
        return "coach_dashboard";
    }

    @GetMapping("/client/dashboard")
    public String clientDashboard() {
        return "client_dashboard";
    }
}