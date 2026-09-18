package com.example.studentplacementmanagement.dto.response;

import com.example.studentplacementmanagement.enums.DriveStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobDriveResponseDTO {

    private Long id;

    private Long companyId;

    private String companyName;

    private String jobTitle;

    private String jobType;

    private String location;

    private BigDecimal minimumCgpa;

    private Integer maximumBacklogs;

    private String eligibleDepartment;

    private Integer graduationYear;

    private BigDecimal salaryPackage;

    private String skills;

    private LocalDateTime driveDate;

    private LocalDateTime applicationDeadline;

    private DriveStatus status;
}