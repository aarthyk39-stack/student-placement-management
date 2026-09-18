package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.response.NotificationResponseDTO;
import com.example.studentplacementmanagement.entity.Notification;
import com.example.studentplacementmanagement.entity.User;
import com.example.studentplacementmanagement.enums.NotificationStatus;
import com.example.studentplacementmanagement.enums.NotificationType;
import com.example.studentplacementmanagement.repository.NotificationRepository;
import com.example.studentplacementmanagement.repository.UserRepository;
import com.example.studentplacementmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationResponseDTO createNotification(
            Long userId,
            String title,
            String message,
            NotificationType type) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with ID: " + userId
                        )
                );

        Notification notification =
                Notification.builder()
                        .user(user)
                        .title(title)
                        .message(message)
                        .type(type)
                        .status(NotificationStatus.UNREAD)
                        .build();

        Notification savedNotification =
                notificationRepository.save(notification);

        return mapToResponse(savedNotification);
    }

    @Override
    @Transactional
    public NotificationResponseDTO createScheduledNotification(
            Long userId,
            String title,
            String message,
            NotificationType type,
            LocalDateTime scheduledAt) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with ID: " + userId
                        )
                );

        Notification notification =
                Notification.builder()
                        .user(user)
                        .title(title)
                        .message(message)
                        .type(type)
                        .status(NotificationStatus.PENDING)
                        .scheduledAt(scheduledAt)
                        .build();

        Notification savedNotification =
                notificationRepository.save(notification);

        return mapToResponse(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getUserNotifications(
            Long userId,
            Pageable pageable) {

        return notificationRepository
                .findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByStatus(
            NotificationStatus status) {

        return notificationRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByType(
            NotificationType type) {

        return notificationRepository
                .findByType(type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserNotifications(
            Long userId,
            NotificationStatus status) {

        return notificationRepository
                .countByUserIdAndStatus(
                        userId,
                        status
                );
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found with ID: "
                                                + notificationId
                                )
                        );

        notification.setStatus(
                NotificationStatus.READ
        );

        notification.setReadAt(
                LocalDateTime.now()
        );

        notificationRepository.save(notification);
    }

    private NotificationResponseDTO mapToResponse(
            Notification notification) {

        return NotificationResponseDTO.builder()
                .notificationId(
                        notification.getNotificationId()
                )
                .userId(
                        notification.getUser().getId()
                )
                .title(
                        notification.getTitle()
                )
                .message(
                        notification.getMessage()
                )
                .type(
                        notification.getType()
                )
                .status(
                        notification.getStatus()
                )
                .scheduledAt(
                        notification.getScheduledAt()
                )
                .readAt(
                        notification.getReadAt()
                )
                .createdDate(
                        notification.getCreatedDate()
                )
                .build();
    }
}