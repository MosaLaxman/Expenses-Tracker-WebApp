package com.SpringBootMVC.ExpensesTracker.service;

import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InsightServiceImpl implements InsightService {
    private final DashboardService dashboardService;
    private final NotificationService notificationService;

    public InsightServiceImpl(DashboardService dashboardService, NotificationService notificationService) {
        this.dashboardService = dashboardService;
        this.notificationService = notificationService;
    }

    @Override
    public List<String> insights(int clientId) {
        List<String> messages = new ArrayList<>();
        Map<String, Object> comparison = dashboardService.monthComparison(clientId, YearMonth.now().toString());
        double change = (double) comparison.getOrDefault("changePercent", 0.0);
        if (change > 20.0) {
            String msg = "Your spending increased by " + change + "% compared to last month.";
            messages.add(msg);
            notificationService.createNotification(clientId, msg, com.SpringBootMVC.ExpensesTracker.entity.NotificationType.INSIGHT);
        }

        Map<String, Integer> categoryDistribution = dashboardService.categoryDistribution(clientId);
        for (Map.Entry<String, Integer> entry : categoryDistribution.entrySet()) {
            if (entry.getValue() > 1000) {
                messages.add("High spending detected in " + entry.getKey() + ": " + entry.getValue());
            }
        }

        if (messages.isEmpty()) {
            messages.add("No unusual spending patterns detected this period.");
        }
        return messages;
    }
}
