package com.example.studentplacementmanagement.entity;

import com.example.studentplacementmanagement.enums.NotificationStatus;
import com.example.studentplacementmanagement.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_user", columnList = "user_id"),
                @Index(name = "idx_notification_status", columnList = "status"),
                @Index(name = "idx_notification_scheduled", columnList = "scheduled_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            nullable = false,
            length = 100
    )
    private String title;

    @Column(
            nullable = false,
            length = 500
    )
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private NotificationStatus status =
            NotificationStatus.UNREAD;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}