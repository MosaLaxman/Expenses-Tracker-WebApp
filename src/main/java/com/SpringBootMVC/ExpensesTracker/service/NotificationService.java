package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.NotificationDTO;
import com.SpringBootMVC.ExpensesTracker.entity.NotificationType;
import org.springframework.data.domain.Page;

public interface NotificationService {
    void createNotification(int clientId, String message, NotificationType type);
    Page<NotificationDTO> getNotifications(int clientId, int page, int size);
    void markAsRead(int clientId, int notificationId);
    void markAllAsRead(int clientId);
    long unreadCount(int clientId);
}
