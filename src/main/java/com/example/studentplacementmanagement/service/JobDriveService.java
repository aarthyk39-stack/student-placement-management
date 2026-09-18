package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.JobDriveRequestDTO;
import com.example.studentplacementmanagement.dto.response.JobDriveResponseDTO;
import com.example.studentplacementmanagement.enums.DriveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface JobDriveService {

    JobDriveResponseDTO saveJobDrive(
            JobDriveRequestDTO requestDTO
    );

    JobDriveResponseDTO getJobDriveById(
            Long id
    );

    Page<JobDriveResponseDTO> getAllJobDrives(
            Pageable pageable
    );

    Page<JobDriveResponseDTO> searchJobDrives(
            String keyword,
            Pageable pageable
    );

    List<JobDriveResponseDTO> getJobDrivesByCompany(
            Long companyId
    );

    List<JobDriveResponseDTO> getJobDrivesByStatus(
            DriveStatus status
    );

    List<JobDriveResponseDTO> getEligibleJobDrives(
            BigDecimal cgpa,
            Integer backlogs,
            String department
    );

    List<JobDriveResponseDTO> getOpenJobDrives();

    void updateDriveStatus(
            Long id,
            DriveStatus status
    );

    long getDriveCountByStatus(
            DriveStatus status
    );

    void deleteJobDrive(Long id);
}