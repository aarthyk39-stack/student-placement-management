package com.example.studentplacementmanagement.entity;

import com.example.studentplacementmanagement.enums.InterviewResult;
import com.example.studentplacementmanagement.enums.InterviewType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "interviews",
        indexes = {
                @Index(name = "idx_interview_application", columnList = "application_id"),
                @Index(name = "idx_interview_date", columnList = "scheduled_at"),
                @Index(name = "idx_interview_result", columnList = "result")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false, length = 30)
    private InterviewType interviewType;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(length = 200)
    private String location;

    @Column(name = "interviewer_name", length = 100)
    private String interviewerName;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private InterviewResult result;

    @Column(length = 500)
    private String feedback;

    @Version
    @Builder.Default
    private Long version = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}