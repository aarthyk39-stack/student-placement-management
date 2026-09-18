package com.example.studentplacementmanagement.entity;

import com.example.studentplacementmanagement.enums.DriveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "job_drives",
        indexes = {
                @Index(name = "idx_drive_title", columnList = "job_title"),
                @Index(name = "idx_drive_status", columnList = "status"),
                @Index(name = "idx_drive_deadline", columnList = "application_deadline"),
                @Index(name = "idx_drive_company", columnList = "company_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    @Column(length = 100)
    private String jobType;

    @Column(length = 100)
    private String location;

    @Column(name = "minimum_cgpa", precision = 4, scale = 2)
    private BigDecimal minimumCgpa;

    @Column(name = "maximum_backlogs")
    private Integer maximumBacklogs;

    @Column(length = 100)
    private String eligibleDepartment;

    @Column(name = "graduation_year", length = 20)
    private Integer graduationYear;

    @Column(name = "salary_package")
    private BigDecimal salaryPackage;

    @Column(length = 100)
    private String skills;

    @Column(name = "drive_date")
    private LocalDateTime driveDate;

    @Column(name = "application_deadline", nullable = false)
    private LocalDateTime applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private DriveStatus status = DriveStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Builder.Default
    private Long version = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "jobDrive")
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

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