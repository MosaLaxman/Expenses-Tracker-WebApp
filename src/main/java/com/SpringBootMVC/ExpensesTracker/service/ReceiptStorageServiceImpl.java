package com.SpringBootMVC.ExpensesTracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ReceiptStorageServiceImpl implements ReceiptStorageService {
    private final Path baseDir;

    public ReceiptStorageServiceImpl(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, int expenseId) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Files.createDirectories(baseDir.resolve("receipts"));
            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "receipt.bin";
            String safe = original.replaceAll("[^a-zA-Z0-9._-]", "_");
            String name = expenseId + "_" + UUID.randomUUID() + "_" + safe;
            Path target = baseDir.resolve("receipts").resolve(name);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "receipts/" + name;
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public Path resolve(String relativePath) {
        return baseDir.resolve(relativePath).normalize();
    }
}
