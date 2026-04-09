package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.SharedExpenseRequestDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.SplitGroupDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.service.SharedExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/split")
public class SharedExpenseController {
    private final SharedExpenseService sharedExpenseService;

    public SharedExpenseController(SharedExpenseService sharedExpenseService) {
        this.sharedExpenseService = sharedExpenseService;
    }

    @PostMapping("/groups")
    public ResponseEntity<SplitGroupDTO> createGroup(@RequestBody Map<String, String> payload, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        SplitGroupDTO group = sharedExpenseService.createGroup(client.getId(), payload.get("name"));
        return group == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(group);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<SplitGroupDTO>> groups(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(sharedExpenseService.groups(client.getId()));
    }

    @PostMapping("/groups/{groupId}/members")
    public ResponseEntity<Void> addMember(@PathVariable int groupId, @RequestBody Map<String, Integer> payload, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        Integer memberClientId = payload.get("memberClientId");
        if (memberClientId == null) {
            return ResponseEntity.badRequest().build();
        }
        sharedExpenseService.addMember(groupId, client.getId(), memberClientId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/expenses")
    public ResponseEntity<Void> addSharedExpense(@RequestBody SharedExpenseRequestDTO dto, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        sharedExpenseService.addSharedExpense(dto, client.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups/{groupId}/balances")
    public ResponseEntity<Map<Integer, Integer>> balances(@PathVariable int groupId, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(sharedExpenseService.balances(groupId, client.getId()));
    }
}
