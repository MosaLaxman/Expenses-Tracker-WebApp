package com.SpringBootMVC.ExpensesTracker.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleUnhandledExceptions(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled error on path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        model.addAttribute("errorPath", request.getRequestURI());
        String message = ex.getMessage() != null ? ex.getMessage() : "Unexpected server error";
        model.addAttribute("errorMessage", "Something went wrong while processing your request. Cause: " + message);
        return "error-page";
    }
}
