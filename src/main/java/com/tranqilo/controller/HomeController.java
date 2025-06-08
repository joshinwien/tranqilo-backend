package com.tranqilo.controller;

import com.tranqilo.model.User;
import com.tranqilo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

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
        return "home";
    }

    @GetMapping("/coach/dashboard")
    @Transactional
    public String coachDashboard(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Cannot find logged in coach"));
        model.addAttribute("clients", currentUser.getClients());
        return "coach_dashboard";
    }

    @GetMapping("/client/dashboard")
    @Transactional
    public String clientDashboard(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Cannot find logged in client"));
        model.addAttribute("coach", currentUser.getCoach());
        return "client_dashboard";
    }
}