package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.ApplicationRequestDTO;
import com.example.studentplacementmanagement.dto.response.ApplicationResponseDTO;
import com.example.studentplacementmanagement.entity.Application;
import com.example.studentplacementmanagement.entity.JobDrive;
import com.example.studentplacementmanagement.entity.Student;
import com.example.studentplacementmanagement.enums.ApplicationStatus;
import com.example.studentplacementmanagement.enums.DriveStatus;
import com.example.studentplacementmanagement.exception.BusinessValidationException;
import com.example.studentplacementmanagement.exception.DuplicateResourceException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.mapper.ApplicationMapper;
import com.example.studentplacementmanagement.repository.ApplicationRepository;
import com.example.studentplacementmanagement.repository.JobDriveRepository;
import com.example.studentplacementmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final StudentRepository studentRepository;

    private final JobDriveRepository jobDriveRepository;

    private final ApplicationMapper applicationMapper;


    // =====================================================
    // CREATE + UPDATE APPLICATION
    // =====================================================

    @Override
    public ApplicationResponseDTO saveApplication(
            ApplicationRequestDTO requestDTO) {

        Application application;

        // =================================================
        // CREATE
        // =================================================

        if (requestDTO.getId() == null) {

            log.info(
                    "Creating application for student ID: {} " +
                            "and job drive ID: {}",
                    requestDTO.getStudentId(),
                    requestDTO.getJobDriveId()
            );

            // ---------------------------------------------
            // GET STUDENT
            // ---------------------------------------------

            Student student = getStudent(
                    requestDTO.getStudentId()
            );

            // ---------------------------------------------
            // GET JOB DRIVE
            // ---------------------------------------------

            JobDrive jobDrive = getOpenJobDrive(
                    requestDTO.getJobDriveId()
            );

            // ---------------------------------------------
            // CHECK DEADLINE
            // ---------------------------------------------

            validateApplicationDeadline(jobDrive);

            // ---------------------------------------------
            // CHECK STUDENT ELIGIBILITY
            // ---------------------------------------------

            validateStudentEligibility(
                    student,
                    jobDrive
            );

            // ---------------------------------------------
            // CHECK DUPLICATE APPLICATION
            // ---------------------------------------------

            if (applicationRepository
                    .existsByStudentIdAndJobDriveId(
                            student.getId(),
                            jobDrive.getId()
                    )) {

                throw new DuplicateResourceException(
                        "Student has already applied for this job drive"
                );
            }

            // ---------------------------------------------
            // DTO → ENTITY
            // ---------------------------------------------

            application =
                    applicationMapper.toEntity(requestDTO);

            // ---------------------------------------------
            // SET ACTUAL ENTITIES
            // ---------------------------------------------

            application.setStudent(student);

            application.setJobDrive(jobDrive);

            // ---------------------------------------------
            // DEFAULT STATUS
            // ---------------------------------------------

            application.setStatus(
                    ApplicationStatus.APPLIED
            );

            // ---------------------------------------------
            // APPLICATION TIME
            // ---------------------------------------------

            application.setAppliedAt(
                    LocalDateTime.now()
            );
        }

        // =================================================
        // UPDATE
        // =================================================

        else {

            log.info(
                    "Updating application with ID: {}",
                    requestDTO.getId()
            );

            application =
                    applicationRepository.findById(
                            requestDTO.getId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Application not found with ID: "
                                            + requestDTO.getId()
                            )
                    );

            // ---------------------------------------------
            // GET STUDENT
            // ---------------------------------------------

            Student student = getStudent(
                    requestDTO.getStudentId()
            );

            // ---------------------------------------------
            // GET JOB DRIVE
            // ---------------------------------------------

            JobDrive jobDrive =
                    jobDriveRepository.findById(
                            requestDTO.getJobDriveId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Job drive not found with ID: "
                                            + requestDTO.getJobDriveId()
                            )
                    );

            // ---------------------------------------------
            // PREVENT STUDENT / DRIVE CHANGE
            // ---------------------------------------------

            if (!application.getStudent()
                    .getId()
                    .equals(student.getId())) {

                throw new BusinessValidationException(
                        "Student cannot be changed for an existing application"
                );
            }

            if (!application.getJobDrive()
                    .getId()
                    .equals(jobDrive.getId())) {

                throw new BusinessValidationException(
                        "Job drive cannot be changed for an existing application"
                );
            }

            // ---------------------------------------------
            // UPDATE REMARKS ONLY
            // ---------------------------------------------

            application.setRemarks(
                    requestDTO.getRemarks()
            );

            /*
             * Status should NOT be updated here.
             *
             * Status changes must go through
             * updateApplicationStatus().
             */
        }

        // =================================================
        // SAVE
        // =================================================

        Application savedApplication =
                applicationRepository.save(application);

        log.info(
                "Application saved successfully with ID: {}",
                savedApplication.getId()
        );

        return buildResponse(savedApplication);
    }


    // =====================================================
    // GET APPLICATION BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponseDTO getApplicationById(
            Long id) {

        log.info(
                "Fetching application with ID: {}",
                id
        );

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with ID: "
                                                + id
                                )
                        );

        return buildResponse(application);
    }


    // =====================================================
    // GET ALL APPLICATIONS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> getAllApplications(
            Pageable pageable) {

        log.info("Fetching all applications");

        return applicationRepository
                .findAll(pageable)
                .map(this::buildResponse);
    }


    // =====================================================
    // GET APPLICATIONS BY STUDENT
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> getApplicationsByStudent(
            Long studentId,
            Pageable pageable) {

        log.info(
                "Fetching applications for student ID: {}",
                studentId
        );

        // Verify student
        if (!studentRepository.existsById(studentId)) {

            throw new ResourceNotFoundException(
                    "Student not found with ID: "
                            + studentId
            );
        }

        return applicationRepository
                .findByStudentId(
                        studentId,
                        pageable
                )
                .map(this::buildResponse);
    }


    // =====================================================
    // GET APPLICATIONS BY JOB DRIVE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResponseDTO> getApplicationsByJobDrive(
            Long jobDriveId,
            Pageable pageable) {

        log.info(
                "Fetching applications for job drive ID: {}",
                jobDriveId
        );

        // Verify job drive
        if (!jobDriveRepository.existsById(jobDriveId)) {

            throw new ResourceNotFoundException(
                    "Job drive not found with ID: "
                            + jobDriveId
            );
        }

        return applicationRepository
                .findByJobDriveId(
                        jobDriveId,
                        pageable
                )
                .map(this::buildResponse);
    }


    // =====================================================
    // GET APPLICATIONS BY STATUS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getApplicationsByStatus(
            ApplicationStatus status) {

        log.info(
                "Fetching applications with status: {}",
                status
        );

        return applicationRepository
                .findByStatus(status)
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // STUDENT PLACEMENT HISTORY
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO> getStudentPlacementHistory(
            Long studentId) {

        log.info(
                "Fetching placement history for student ID: {}",
                studentId
        );

        if (!studentRepository.existsById(studentId)) {

            throw new ResourceNotFoundException(
                    "Student not found with ID: "
                            + studentId
            );
        }

        return applicationRepository
                .findStudentPlacementHistory(studentId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // UPDATE APPLICATION STATUS
    // =====================================================

    @Override
    public void updateApplicationStatus(
            Long id,
            ApplicationStatus newStatus) {

        log.info(
                "Updating application {} status to {}",
                id,
                newStatus
        );

        if (newStatus == null) {

            throw new BusinessValidationException(
                    "Application status is required"
            );
        }

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with ID: "
                                                + id
                                )
                        );

        ApplicationStatus currentStatus =
                application.getStatus();

        // ---------------------------------------------
        // VALIDATE STATUS TRANSITION
        // ---------------------------------------------

        validateStatusTransition(
                currentStatus,
                newStatus
        );

        // ---------------------------------------------
        // SHORTLISTED
        // ---------------------------------------------

        if (newStatus ==
                ApplicationStatus.SHORTLISTED) {

            application.setShortlistedAt(
                    LocalDateTime.now()
            );
        }

        // ---------------------------------------------
        // PLACED
        // ---------------------------------------------

        if (newStatus ==
                ApplicationStatus.PLACED) {

            if (application.getStudent() != null) {

                application.getStudent()
                        .setIsPlaced(true);
            }
        }

        // ---------------------------------------------
        // UPDATE STATUS
        // ---------------------------------------------

        application.setStatus(newStatus);

        applicationRepository.save(application);

        log.info(
                "Application status updated successfully"
        );
    }


    // =====================================================
    // DELETE APPLICATION
    // =====================================================

    @Override
    public void deleteApplication(Long id) {

        log.info(
                "Deleting application with ID: {}",
                id
        );

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found with ID: "
                                                + id
                                )
                        );

        /*
         * We should not physically delete application
         * records because they are part of placement history.
         *
         * Instead mark application as WITHDRAWN.
         */

        if (application.getStatus() ==
                ApplicationStatus.PLACED) {

            throw new BusinessValidationException(
                    "Placed application cannot be withdrawn"
            );
        }

        application.setStatus(
                ApplicationStatus.WITHDRAWN
        );

        applicationRepository.save(application);

        log.info(
                "Application withdrawn successfully"
        );
    }


    // =====================================================
    // COUNT BY STATUS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public long getApplicationCountByStatus(
            ApplicationStatus status) {

        return applicationRepository
                .countByStatus(status);
    }


    // =====================================================
    // COUNT BY JOB DRIVE
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public long getApplicationCountByJobDrive(
            Long jobDriveId) {

        return applicationRepository
                .countByJobDriveId(jobDriveId);
    }


    // =====================================================
    // GET STUDENT
    // =====================================================

    private Student getStudent(Long studentId) {

        return studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with ID: "
                                        + studentId
                        )
                );
    }


    // =====================================================
    // GET OPEN JOB DRIVE
    // =====================================================

    private JobDrive getOpenJobDrive(
            Long jobDriveId) {

        JobDrive jobDrive =
                jobDriveRepository.findById(jobDriveId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job drive not found with ID: "
                                                + jobDriveId
                                )
                        );

        if (jobDrive.getStatus() !=
                DriveStatus.OPEN) {

            throw new BusinessValidationException(
                    "Job drive is not currently open"
            );
        }

        return jobDrive;
    }


    // =====================================================
    // DEADLINE VALIDATION
    // =====================================================

    private void validateApplicationDeadline(
            JobDrive jobDrive) {

        if (jobDrive.getApplicationDeadline() == null) {

            throw new BusinessValidationException(
                    "Application deadline is not configured"
            );
        }

        if (LocalDateTime.now()
                .isAfter(
                        jobDrive.getApplicationDeadline()
                )) {

            throw new BusinessValidationException(
                    "Application deadline has expired"
            );
        }
    }


    // =====================================================
    // STUDENT ELIGIBILITY
    // =====================================================

    private void validateStudentEligibility(
            Student student,
            JobDrive jobDrive) {

        // ---------------------------------------------
        // CGPA CHECK
        // ---------------------------------------------

        if (jobDrive.getMinimumCgpa() != null
                && student.getCgpa().compareTo(
                jobDrive.getMinimumCgpa()
        ) < 0) {

            throw new BusinessValidationException(
                    "Student is not eligible based on CGPA"
            );
        }

        // ---------------------------------------------
        // BACKLOG CHECK
        // ---------------------------------------------

        if (jobDrive.getMaximumBacklogs() != null
                && student.getBacklogs()
                > jobDrive.getMaximumBacklogs()) {

            throw new BusinessValidationException(
                    "Student is not eligible based on backlogs"
            );
        }

        // ---------------------------------------------
        // DEPARTMENT CHECK
        // ---------------------------------------------

        if (jobDrive.getEligibleDepartment() != null
                && !jobDrive.getEligibleDepartment()
                .equalsIgnoreCase(
                        student.getDepartment()
                )) {

            throw new BusinessValidationException(
                    "Student is not eligible for this department"
            );
        }

        // ---------------------------------------------
        // GRADUATION YEAR CHECK
        // ---------------------------------------------

        if (jobDrive.getGraduationYear() != null
                && !jobDrive.getGraduationYear()
                .equals(student.getGraduationYear())) {

            throw new BusinessValidationException(
                    "Student is not eligible for this graduation year"
            );
        }

        // ---------------------------------------------
        // ALREADY PLACED CHECK
        // ---------------------------------------------

        if (Boolean.TRUE.equals(
                student.getIsPlaced())) {

            throw new BusinessValidationException(
                    "Already placed student cannot apply"
            );
        }
    }


    // =====================================================
    // STATUS TRANSITION VALIDATION
    // =====================================================

    private void validateStatusTransition(
            ApplicationStatus currentStatus,
            ApplicationStatus newStatus) {

        if (currentStatus == null) {
            return;
        }

        if (currentStatus ==
                ApplicationStatus.WITHDRAWN) {

            throw new BusinessValidationException(
                    "Withdrawn application cannot be updated"
            );
        }

        if (currentStatus ==
                ApplicationStatus.REJECTED) {

            throw new BusinessValidationException(
                    "Rejected application cannot be moved to another status"
            );
        }

        if (currentStatus ==
                ApplicationStatus.PLACED
                && newStatus !=
                ApplicationStatus.PLACED) {

            throw new BusinessValidationException(
                    "Placed application status cannot be changed"
            );
        }

        // Prevent moving backward
        if (currentStatus ==
                ApplicationStatus.SHORTLISTED
                && newStatus ==
                ApplicationStatus.APPLIED) {

            throw new BusinessValidationException(
                    "Application cannot move backward to APPLIED"
            );
        }

        if (currentStatus ==
                ApplicationStatus.INTERVIEW_SCHEDULED
                && newStatus ==
                ApplicationStatus.APPLIED) {

            throw new BusinessValidationException(
                    "Application cannot move backward to APPLIED"
            );
        }

        if (currentStatus ==
                ApplicationStatus.SELECTED
                && newStatus ==
                ApplicationStatus.APPLIED) {

            throw new BusinessValidationException(
                    "Application cannot move backward to APPLIED"
            );
        }

        if (currentStatus ==
                ApplicationStatus.OFFERED
                && newStatus ==
                ApplicationStatus.APPLIED) {

            throw new BusinessValidationException(
                    "Application cannot move backward to APPLIED"
            );
        }
    }


    // =====================================================
    // BUILD RESPONSE
    // =====================================================

    private ApplicationResponseDTO buildResponse(
            Application application) {

        ApplicationResponseDTO response =
                applicationMapper.toResponseDTO(
                        application
                );

        // Student name
        if (application.getStudent() != null) {

            response.setStudentName(
                    application.getStudent().getName()
            );
        }

        // Job title + company
        if (application.getJobDrive() != null) {

            response.setJobTitle(
                    application.getJobDrive()
                            .getJobTitle()
            );

            if (application.getJobDrive()
                    .getCompany() != null) {

                response.setCompanyName(
                        application.getJobDrive()
                                .getCompany()
                                .getName()
                );
            }
        }

        return response;
    }
}