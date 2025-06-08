package com.tranqilo.controller;

import com.tranqilo.model.Role;
import com.tranqilo.model.User;
import com.tranqilo.repository.UserRepository;
import com.tranqilo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final UserService userService;

    public HomeController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
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
        // This can be a generic landing page for users whose roles are not CLIENT or COACH
        return "home";
    }

    @GetMapping("/coach/dashboard")
    @Transactional
    public String coachDashboard(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Cannot find logged in coach"));
        model.addAttribute("clients", currentUser.getClients());
        model.addAttribute("unassignedClients", userRepository.findByRoleAndCoachIsNull(Role.CLIENT));
        return "coach_dashboard";
    }



    @GetMapping("/client/dashboard")
    @Transactional
    public String clientDashboard(Model model, Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Cannot find logged in client"));
        // We no longer need to pass the client explicitly, the GlobalControllerAdvice handles it.
        // We only need to pass the data specific to this page.
        model.addAttribute("coach", currentUser.getCoach());
        return "client_dashboard";
    }

    @PostMapping("/coach/add-client")
    public String addClient(@RequestParam String clientUsername, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            userService.assignClientToCoach(clientUsername, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Client successfully added!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/coach/dashboard";
    }

    @PostMapping("/coach/remove-client")
    public String removeClient(@RequestParam String clientUsername, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            userService.removeClientFromCoach(clientUsername, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Client successfully removed!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/coach/dashboard";
    }
}