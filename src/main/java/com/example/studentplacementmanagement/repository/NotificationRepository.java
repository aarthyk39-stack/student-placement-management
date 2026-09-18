package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.Notification;
import com.example.studentplacementmanagement.enums.NotificationStatus;
import com.example.studentplacementmanagement.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    Page<Notification> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<Notification> findByStatus(
            NotificationStatus status
    );

    List<Notification> findByType(
            NotificationType type
    );

    List<Notification> findByScheduledAtBeforeAndStatus(
            LocalDateTime dateTime,
            NotificationStatus status
    );

    long countByUserIdAndStatus(
            Long userId,
            NotificationStatus status
    );

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.status = :status
            AND n.scheduledAt <= :currentTime
            ORDER BY n.scheduledAt ASC
            """)
    List<Notification> findPendingScheduledNotifications(
            @Param("status") NotificationStatus status,
            @Param("currentTime") LocalDateTime currentTime
    );
}