package com.example.studentplacementmanagement.dto.request;

import com.example.studentplacementmanagement.enums.DriveStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobDriveRequestDTO {

    private Long id;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotBlank(message = "Job location is required")
    private String location;

    @NotNull(message = "Minimum CGPA is required")
    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    private BigDecimal minimumCgpa;

    @NotNull(message = "Maximum backlogs is required")
    @Min(value = 0, message = "Maximum backlogs cannot be negative")
    private Integer maximumBacklogs;

    private String eligibleDepartment;

    @NotNull(message = "Graduation year is required")
    @Min(value = 2000, message = "Invalid graduation year")
    private Integer graduationYear;

    @NotNull(message = "Salary package is required")
    @DecimalMin(value = "0.0", message = "Salary package cannot be negative")
    private BigDecimal salaryPackage;

    private String skills;

    @NotNull(message = "Drive date is required")
    private LocalDateTime driveDate;

    @NotNull(message = "Application deadline is required")
    private LocalDateTime applicationDeadline;

    @NotNull(message = "Drive status is required")
    private DriveStatus status;
}