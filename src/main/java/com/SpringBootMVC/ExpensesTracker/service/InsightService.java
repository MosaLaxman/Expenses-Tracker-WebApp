package com.SpringBootMVC.ExpensesTracker.service;

import java.util.List;

public interface InsightService {
    List<String> insights(int clientId);
}
