package com.SpringBootMVC.ExpensesTracker.config;

import com.SpringBootMVC.ExpensesTracker.entity.Category;
import com.SpringBootMVC.ExpensesTracker.entity.Role;
import com.SpringBootMVC.ExpensesTracker.repository.CategoryRepository;
import com.SpringBootMVC.ExpensesTracker.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class BootstrapDataConfig {
    private static final Logger log = LoggerFactory.getLogger(BootstrapDataConfig.class);

    @Bean
    @Transactional
    public CommandLineRunner seedReferenceData(RoleRepository roleRepository, CategoryRepository categoryRepository) {
        return args -> {
            seedRole(roleRepository, 1, "ROLE_STANDARD");

            Map<Integer, String> categoryMap = new LinkedHashMap<>();
            categoryMap.put(1, "groceries");
            categoryMap.put(2, "Utilities(bills)");
            categoryMap.put(3, "transportation");
            categoryMap.put(4, "dining out");
            categoryMap.put(5, "entertainment");
            categoryMap.put(6, "shopping");
            categoryMap.put(7, "travel");
            categoryMap.put(8, "education");

            for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
                seedCategory(categoryRepository, entry.getKey(), entry.getValue());
            }
        };
    }

    private void seedRole(RoleRepository roleRepository, int roleId, String roleName) {
        if (roleRepository.findByName(roleName) != null) {
            return;
        }
        if (roleRepository.existsById(roleId)) {
            log.warn("Role id {} already exists; skipping seed for {}", roleId, roleName);
            return;
        }
        Role role = new Role();
        role.setId(roleId);
        role.setName(roleName);
        roleRepository.save(role);
        log.info("Seeded role {}", roleName);
    }

    private void seedCategory(CategoryRepository categoryRepository, int categoryId, String categoryName) {
        if (categoryRepository.findByName(categoryName) != null) {
            return;
        }
        if (categoryRepository.existsById(categoryId)) {
            log.warn("Category id {} already exists; skipping seed for {}", categoryId, categoryName);
            return;
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        categoryRepository.save(category);
        log.info("Seeded category {}", categoryName);
    }
}
