package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.RecurringExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.service.RecurringExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recurring")
public class RecurringExpenseController {
    private final RecurringExpenseService recurringExpenseService;

    public RecurringExpenseController(RecurringExpenseService recurringExpenseService) {
        this.recurringExpenseService = recurringExpenseService;
    }

    @PostMapping
    public ResponseEntity<RecurringExpenseDTO> create(@RequestBody RecurringExpenseDTO dto, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        dto.setClientId(client.getId());
        RecurringExpenseDTO created = recurringExpenseService.create(dto);
        return created == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<RecurringExpenseDTO>> list(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(recurringExpenseService.listByClient(client.getId()));
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable int id, @RequestBody Map<String, Boolean> payload, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        boolean active = payload.getOrDefault("active", true);
        recurringExpenseService.toggle(id, client.getId(), active);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/run-now")
    public ResponseEntity<Map<String, Integer>> runNow(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        int created = recurringExpenseService.processDueRecurringExpenses();
        return ResponseEntity.ok(Map.of("createdExpenses", created));
    }
}
