package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.ApplicationRequestDTO;
import com.example.studentplacementmanagement.dto.response.ApplicationResponseDTO;
import com.example.studentplacementmanagement.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {

    ApplicationResponseDTO saveApplication(
            ApplicationRequestDTO requestDTO
    );

    ApplicationResponseDTO getApplicationById(
            Long id
    );

    Page<ApplicationResponseDTO> getAllApplications(
            Pageable pageable
    );

    Page<ApplicationResponseDTO> getApplicationsByStudent(
            Long studentId,
            Pageable pageable
    );

    Page<ApplicationResponseDTO> getApplicationsByJobDrive(
            Long jobDriveId,
            Pageable pageable
    );

    List<ApplicationResponseDTO> getApplicationsByStatus(
            ApplicationStatus status
    );

    List<ApplicationResponseDTO> getStudentPlacementHistory(
            Long studentId
    );

    void updateApplicationStatus(
            Long id,
            ApplicationStatus status
    );

    void deleteApplication(Long id);

    long getApplicationCountByStatus(
            ApplicationStatus status
    );

    long getApplicationCountByJobDrive(
            Long jobDriveId
    );
}