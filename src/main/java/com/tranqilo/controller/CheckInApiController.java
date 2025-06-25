package com.tranqilo.controller;

import com.tranqilo.model.CheckIn;
import com.tranqilo.repository
        .CheckInRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/check-ins")
public class CheckInApiController {

    private final CheckInRepository checkInRepository;

    public CheckInApiController(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    @GetMapping
    public List<CheckIn> getAllCheckIns() {
        return checkInRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckIn> getCheckInById(@PathVariable Long id) {
        return checkInRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CheckIn createCheckIn(@RequestBody CheckIn checkIn) {
        return checkInRepository.save(checkIn);
    }
}