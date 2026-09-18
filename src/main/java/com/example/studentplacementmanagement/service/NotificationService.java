package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.response.NotificationResponseDTO;
import com.example.studentplacementmanagement.enums.NotificationStatus;
import com.example.studentplacementmanagement.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {

    NotificationResponseDTO createNotification(
            Long userId,
            String title,
            String message,
            NotificationType type
    );

    NotificationResponseDTO createScheduledNotification(
            Long userId,
            String title,
            String message,
            NotificationType type,
            LocalDateTime scheduledAt
    );

    Page<NotificationResponseDTO> getUserNotifications(
            Long userId,
            Pageable pageable
    );

    List<NotificationResponseDTO> getNotificationsByStatus(
            NotificationStatus status
    );

    List<NotificationResponseDTO> getNotificationsByType(
            NotificationType type
    );

    long countUserNotifications(
            Long userId,
            NotificationStatus status
    );

    void markAsRead(Long notificationId);
}