package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseTemplateDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Category;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.ExpenseTemplate;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseTemplateRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseTemplateServiceImpl implements ExpenseTemplateService {
    private final ExpenseTemplateRepository expenseTemplateRepository;
    private final ClientService clientService;
    private final CategoryService categoryService;

    public ExpenseTemplateServiceImpl(ExpenseTemplateRepository expenseTemplateRepository, ClientService clientService, CategoryService categoryService) {
        this.expenseTemplateRepository = expenseTemplateRepository;
        this.clientService = clientService;
        this.categoryService = categoryService;
    }

    @Override
    @Transactional
    public ExpenseTemplateDTO saveTemplate(ExpenseTemplateDTO dto) {
        Client client = clientService.findClientById(dto.getClientId());
        Category category = categoryService.findCategoryByName(dto.getCategory());
        if (client == null || category == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            return null;
        }

        ExpenseTemplate template = new ExpenseTemplate();
        template.setClient(client);
        template.setCategory(category);
        template.setName(dto.getName().trim());
        template.setDefaultAmount(dto.getDefaultAmount());
        template.setDefaultDescription(dto.getDefaultDescription());
        expenseTemplateRepository.save(template);

        ExpenseTemplateDTO result = new ExpenseTemplateDTO();
        result.setId(template.getId());
        result.setClientId(client.getId());
        result.setName(template.getName());
        result.setCategory(category.getName());
        result.setDefaultAmount(template.getDefaultAmount());
        result.setDefaultDescription(template.getDefaultDescription());
        return result;
    }

    @Override
    public List<ExpenseTemplateDTO> list(int clientId) {
        List<ExpenseTemplate> templates = expenseTemplateRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        List<ExpenseTemplateDTO> result = new ArrayList<>();
        for (ExpenseTemplate template : templates) {
            ExpenseTemplateDTO dto = new ExpenseTemplateDTO();
            dto.setId(template.getId());
            dto.setClientId(clientId);
            dto.setName(template.getName());
            dto.setCategory(template.getCategory() != null ? template.getCategory().getName() : "");
            dto.setDefaultAmount(template.getDefaultAmount());
            dto.setDefaultDescription(template.getDefaultDescription());
            result.add(dto);
        }
        return result;
    }
}
