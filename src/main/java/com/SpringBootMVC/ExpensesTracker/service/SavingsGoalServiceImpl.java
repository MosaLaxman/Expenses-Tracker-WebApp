package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.SavingsGoalDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.SavingsGoal;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import com.SpringBootMVC.ExpensesTracker.repository.SavingsGoalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class SavingsGoalServiceImpl implements SavingsGoalService {
    private final SavingsGoalRepository savingsGoalRepository;
    private final ClientService clientService;
    private final ExpenseRepository expenseRepository;

    public SavingsGoalServiceImpl(SavingsGoalRepository savingsGoalRepository, ClientService clientService, ExpenseRepository expenseRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.clientService = clientService;
        this.expenseRepository = expenseRepository;
    }

    @Override
    @Transactional
    public SavingsGoalDTO createGoal(SavingsGoalDTO dto) {
        Client client = clientService.findClientById(dto.getClientId());
        if (client == null || dto.getTargetAmount() <= 0 || dto.getName() == null || dto.getName().trim().isEmpty()) {
            return null;
        }
        SavingsGoal goal = new SavingsGoal();
        goal.setClient(client);
        goal.setName(dto.getName().trim());
        goal.setTargetAmount(dto.getTargetAmount());
        if (dto.getDeadline() != null && !dto.getDeadline().isEmpty()) {
            goal.setDeadline(LocalDate.parse(dto.getDeadline()));
        }
        savingsGoalRepository.save(goal);
        dto.setId(goal.getId());
        return dto;
    }

    @Override
    public List<SavingsGoalDTO> activeGoals(int clientId, String monthKey) {
        String key = monthKey != null && monthKey.length() == 7 ? monthKey : YearMonth.now().toString();
        int currentMonthSpend = expenseRepository.sumByClientMonth(clientId, key);
        List<SavingsGoalDTO> results = new ArrayList<>();
        for (SavingsGoal goal : savingsGoalRepository.findByClientIdAndActiveTrueOrderByCreatedAtDesc(clientId)) {
            SavingsGoalDTO dto = new SavingsGoalDTO();
            dto.setId(goal.getId());
            dto.setClientId(clientId);
            dto.setName(goal.getName());
            dto.setTargetAmount(goal.getTargetAmount());
            dto.setDeadline(goal.getDeadline() != null ? goal.getDeadline().toString() : "");
            int projectedSaved = Math.max(0, goal.getTargetAmount() - currentMonthSpend);
            dto.setProjectedSavedAmount(projectedSaved);
            dto.setProgressPercent(goal.getTargetAmount() == 0 ? 0 : Math.min(100, (projectedSaved * 100) / goal.getTargetAmount()));
            results.add(dto);
        }
        return results;
    }
}
