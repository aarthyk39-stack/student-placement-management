package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.response.NotificationResponseDTO;
import com.example.studentplacementmanagement.enums.NotificationStatus;
import com.example.studentplacementmanagement.enums.NotificationType;
import com.example.studentplacementmanagement.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(
        name = "Notification Management",
        description = "APIs for managing user notifications"
)
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Create notification",
            description = "Creates an immediate notification for a user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Notification created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid notification data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @RequestParam Long userId,

            @Parameter(
                    description = "Notification title",
                    example = "Application Shortlisted"
            )
            @RequestParam String title,

            @Parameter(
                    description = "Notification message",
                    example = "Your application has been shortlisted"
            )
            @RequestParam String message,

            @Parameter(
                    description = "Notification type",
                    example = "APPLICATION"
            )
            @RequestParam NotificationType type) {

        NotificationResponseDTO response =
                notificationService.createNotification(
                        userId,
                        title,
                        message,
                        type
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Create scheduled notification",
            description = "Schedules a notification to be delivered at a specific date and time"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Scheduled notification created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid notification data or schedule"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PostMapping("/scheduled")
    public ResponseEntity<NotificationResponseDTO>
    createScheduledNotification(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @RequestParam Long userId,

            @Parameter(
                    description = "Notification title",
                    example = "Interview Reminder"
            )
            @RequestParam String title,

            @Parameter(
                    description = "Notification message",
                    example = "Your interview is scheduled for tomorrow"
            )
            @RequestParam String message,

            @Parameter(
                    description = "Notification type",
                    example = "INTERVIEW"
            )
            @RequestParam NotificationType type,

            @Parameter(
                    description = "Scheduled date and time",
                    example = "2026-09-18T09:00:00"
            )
            @RequestParam LocalDateTime scheduledAt) {

        NotificationResponseDTO response =
                notificationService.createScheduledNotification(
                        userId,
                        title,
                        message,
                        type,
                        scheduledAt
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get user notifications",
            description = "Retrieves notifications belonging to a user with pagination and sorting"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponseDTO>>
    getUserNotifications(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @PathVariable Long userId,
            Pageable pageable) {

        Page<NotificationResponseDTO> response =
                notificationService.getUserNotifications(
                        userId,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get notifications by status",
            description = "Retrieves notifications filtered by notification status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Notifications retrieved successfully"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponseDTO>>
    getNotificationsByStatus(
            @Parameter(
                    description = "Notification status",
                    example = "UNREAD"
            )
            @PathVariable NotificationStatus status) {

        List<NotificationResponseDTO> response =
                notificationService.getNotificationsByStatus(
                        status
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get notifications by type",
            description = "Retrieves notifications filtered by notification type"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Notifications retrieved successfully"
    )
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponseDTO>>
    getNotificationsByType(
            @Parameter(
                    description = "Notification type",
                    example = "INTERVIEW"
            )
            @PathVariable NotificationType type) {

        List<NotificationResponseDTO> response =
                notificationService.getNotificationsByType(
                        type
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Count user notifications",
            description = "Returns the number of notifications for a user with a specific status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Notification count retrieved successfully"
    )
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countUserNotifications(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @PathVariable Long userId,

            @Parameter(
                    description = "Notification status",
                    example = "UNREAD"
            )
            @RequestParam NotificationStatus status) {

        long count =
                notificationService.countUserNotifications(
                        userId,
                        status
                );

        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Mark notification as read",
            description = "Marks a notification as read"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Notification marked as read successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found"
            )
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(
                    description = "Notification ID",
                    example = "1"
            )
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return ResponseEntity.noContent().build();
    }
}