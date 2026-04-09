package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.SavingsGoalDTO;

import java.util.List;

public interface SavingsGoalService {
    SavingsGoalDTO createGoal(SavingsGoalDTO dto);
    List<SavingsGoalDTO> activeGoals(int clientId, String monthKey);
}
