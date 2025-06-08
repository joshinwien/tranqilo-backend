package com.tranqilo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
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