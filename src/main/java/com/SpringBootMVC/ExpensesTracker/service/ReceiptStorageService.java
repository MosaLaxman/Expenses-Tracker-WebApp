package com.SpringBootMVC.ExpensesTracker.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface ReceiptStorageService {
    String store(MultipartFile file, int expenseId);
    Path resolve(String relativePath);
}
