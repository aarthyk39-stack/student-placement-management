package com.example.studentplacementmanagement.dto.response;

import com.example.studentplacementmanagement.enums.NotificationStatus;
import com.example.studentplacementmanagement.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long notificationId;

    private Long userId;

    private String title;

    private String message;

    private NotificationType type;

    private NotificationStatus status;

    private LocalDateTime scheduledAt;

    private LocalDateTime readAt;

    private LocalDateTime createdDate;
}