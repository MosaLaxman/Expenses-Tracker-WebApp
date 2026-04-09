package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseTemplateDTO;

import java.util.List;

public interface ExpenseTemplateService {
    ExpenseTemplateDTO saveTemplate(ExpenseTemplateDTO dto);
    List<ExpenseTemplateDTO> list(int clientId);
}
