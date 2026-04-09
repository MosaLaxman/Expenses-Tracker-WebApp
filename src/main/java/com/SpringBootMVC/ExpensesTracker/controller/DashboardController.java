package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.service.DashboardService;
import com.SpringBootMVC.ExpensesTracker.service.InsightService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    private final InsightService insightService;

    public DashboardController(DashboardService dashboardService, InsightService insightService) {
        this.dashboardService = dashboardService;
        this.insightService = insightService;
    }

    @GetMapping("/monthly-trend")
    public ResponseEntity<Map<String, Integer>> monthlyTrend(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(dashboardService.monthlyTrend(client.getId()));
    }

    @GetMapping("/category-distribution")
    public ResponseEntity<Map<String, Integer>> categoryDistribution(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(dashboardService.categoryDistribution(client.getId()));
    }

    @GetMapping("/month-comparison")
    public ResponseEntity<Map<String, Object>> monthComparison(@RequestParam(required = false) String monthKey, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(dashboardService.monthComparison(client.getId(), monthKey));
    }

    @GetMapping("/insights")
    public ResponseEntity<List<String>> insights(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(insightService.insights(client.getId()));
    }

    @GetMapping("/recent-expenses")
    public ResponseEntity<?> recentExpenses(HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(dashboardService.recentExpenses(client.getId()));
    }
}
