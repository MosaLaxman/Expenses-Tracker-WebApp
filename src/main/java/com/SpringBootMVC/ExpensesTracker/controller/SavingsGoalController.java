package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.SavingsGoalDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.service.SavingsGoalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {
    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @PostMapping
    public ResponseEntity<SavingsGoalDTO> create(@RequestBody SavingsGoalDTO dto, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        dto.setClientId(client.getId());
        SavingsGoalDTO created = savingsGoalService.createGoal(dto);
        return created == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalDTO>> list(@RequestParam(required = false) String monthKey, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(savingsGoalService.activeGoals(client.getId(), monthKey));
    }
}
