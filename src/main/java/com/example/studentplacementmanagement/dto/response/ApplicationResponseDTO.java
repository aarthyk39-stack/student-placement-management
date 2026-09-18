package com.example.studentplacementmanagement.dto.response;

import com.example.studentplacementmanagement.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResponseDTO {

    private Long id;

    private Long studentId;

    private String studentName;

    private Long jobDriveId;

    private String jobTitle;

    private String companyName;

    private ApplicationStatus status;

    private LocalDateTime appliedAt;

    private LocalDateTime shortlistedAt;

    private String remarks;
}