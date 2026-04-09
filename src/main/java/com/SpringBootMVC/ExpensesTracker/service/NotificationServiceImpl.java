package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.NotificationDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Notification;
import com.SpringBootMVC.ExpensesTracker.entity.NotificationType;
import com.SpringBootMVC.ExpensesTracker.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final ClientService clientService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, ClientService clientService) {
        this.notificationRepository = notificationRepository;
        this.clientService = clientService;
    }

    @Override
    @Transactional
    public void createNotification(int clientId, String message, NotificationType type) {
        Client client = clientService.findClientById(clientId);
        if (client == null || message == null || message.trim().isEmpty()) {
            return;
        }
        Notification notification = new Notification();
        notification.setClient(client);
        notification.setMessage(message.trim());
        notification.setType(type != null ? type : NotificationType.INFO);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    @Override
    public Page<NotificationDTO> getNotifications(int clientId, int page, int size) {
        return notificationRepository.findByClientIdOrderByTimestampDesc(clientId, PageRequest.of(page, size))
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void markAsRead(int clientId, int notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getClient() != null && notification.getClient().getId() == clientId) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(int clientId) {
        notificationRepository.findByClientIdOrderByTimestampDesc(clientId, PageRequest.of(0, 500))
                .forEach(notification -> {
                    if (!notification.isRead()) {
                        notification.setRead(true);
                        notificationRepository.save(notification);
                    }
                });
    }

    @Override
    public long unreadCount(int clientId) {
        return notificationRepository.countByClientIdAndReadFalse(clientId);
    }

    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType().name());
        dto.setRead(notification.isRead());
        dto.setTimestamp(notification.getTimestamp() != null ? notification.getTimestamp().toString() : "");
        return dto;
    }
}
