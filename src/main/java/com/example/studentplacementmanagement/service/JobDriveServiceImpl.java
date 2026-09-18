package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.JobDriveRequestDTO;
import com.example.studentplacementmanagement.dto.response.JobDriveResponseDTO;
import com.example.studentplacementmanagement.entity.Company;
import com.example.studentplacementmanagement.entity.JobDrive;
import com.example.studentplacementmanagement.enums.DriveStatus;
import com.example.studentplacementmanagement.exception.BusinessValidationException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.mapper.JobDriveMapper;
import com.example.studentplacementmanagement.repository.CompanyRepository;
import com.example.studentplacementmanagement.repository.JobDriveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobDriveServiceImpl implements JobDriveService {

    private final JobDriveRepository jobDriveRepository;

    private final CompanyRepository companyRepository;

    private final JobDriveMapper jobDriveMapper;


    // =====================================================
    // CREATE + UPDATE JOB DRIVE
    // =====================================================

    @Override
    public JobDriveResponseDTO saveJobDrive(
            JobDriveRequestDTO requestDTO) {

        JobDrive jobDrive;

        // =================================================
        // COMMON VALIDATION
        // =================================================

        validateJobDriveDates(requestDTO);

        validateCgpaAndBacklogs(requestDTO);


        // =================================================
        // CREATE
        // =================================================

        if (requestDTO.getId() == null) {

            log.info(
                    "Creating job drive with title: {}",
                    requestDTO.getJobTitle()
            );

            // Get company
            Company company = getActiveCompany(
                    requestDTO.getCompanyId()
            );

            // DTO → Entity
            jobDrive = jobDriveMapper.toEntity(
                    requestDTO
            );

            // Set actual Company entity
            jobDrive.setCompany(company);

            // New drive default status
            if (jobDrive.getStatus() == null) {

                jobDrive.setStatus(
                        DriveStatus.DRAFT
                );
            }

        }

        else {

            log.info(
                    "Updating job drive with ID: {}",
                    requestDTO.getId()
            );

            jobDrive = jobDriveRepository.findById(
                    requestDTO.getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Job drive not found with ID: "
                                    + requestDTO.getId()
                    )
            );

            Company company = getActiveCompany(
                    requestDTO.getCompanyId()
            );

            // Update company
            jobDrive.setCompany(company);

            // Update fields
            jobDrive.setJobTitle(
                    requestDTO.getJobTitle()
            );

            jobDrive.setJobType(
                    requestDTO.getJobType()
            );

            jobDrive.setLocation(
                    requestDTO.getLocation()
            );

            jobDrive.setMinimumCgpa(
                    requestDTO.getMinimumCgpa()
            );

            jobDrive.setMaximumBacklogs(
                    requestDTO.getMaximumBacklogs()
            );

            jobDrive.setEligibleDepartment(
                    requestDTO.getEligibleDepartment()
            );

            jobDrive.setGraduationYear(
                    requestDTO.getGraduationYear()
            );

            jobDrive.setSalaryPackage(
                    requestDTO.getSalaryPackage()
            );

            jobDrive.setSkills(
                    requestDTO.getSkills()
            );

            jobDrive.setDriveDate(
                    requestDTO.getDriveDate()
            );

            jobDrive.setApplicationDeadline(
                    requestDTO.getApplicationDeadline()
            );

            // Status is controlled separately
            // through updateDriveStatus().
        }


        // =================================================
        // SAVE
        // =================================================

        JobDrive savedJobDrive =
                jobDriveRepository.save(jobDrive);

        log.info(
                "Job drive saved successfully with ID: {}",
                savedJobDrive.getId()
        );

        return buildResponse(savedJobDrive);
    }


    // =====================================================
    // GET BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public JobDriveResponseDTO getJobDriveById(
            Long id) {

        log.info(
                "Fetching job drive with ID: {}",
                id
        );

        JobDrive jobDrive =
                jobDriveRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job drive not found with ID: "
                                                + id
                                )
                        );

        return buildResponse(jobDrive);
    }


    // =====================================================
    // GET ALL
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobDriveResponseDTO> getAllJobDrives(
            Pageable pageable) {

        log.info("Fetching all job drives");

        return jobDriveRepository
                .findAll(pageable)
                .map(this::buildResponse);
    }


    // =====================================================
    // SEARCH
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<JobDriveResponseDTO> searchJobDrives(
            String keyword,
            Pageable pageable) {

        log.info(
                "Searching job drives with keyword: {}",
                keyword
        );

        return jobDriveRepository
                .findByJobTitleContainingIgnoreCase(
                        keyword,
                        pageable
                )
                .map(this::buildResponse);
    }


    // =====================================================
    // GET BY COMPANY
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponseDTO> getJobDrivesByCompany(
            Long companyId) {

        log.info(
                "Fetching job drives for company ID: {}",
                companyId
        );

        // Verify company exists
        if (!companyRepository.existsById(companyId)) {

            throw new ResourceNotFoundException(
                    "Company not found with ID: "
                            + companyId
            );
        }

        return jobDriveRepository
                .findByCompanyId(companyId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponseDTO> getJobDrivesByStatus(
            DriveStatus status) {

        log.info(
                "Fetching job drives with status: {}",
                status
        );

        return jobDriveRepository
                .findByStatus(status)
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // ELIGIBILITY CHECK
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponseDTO> getEligibleJobDrives(
            BigDecimal cgpa,
            Integer backlogs,
            String department) {

        log.info(
                "Finding eligible job drives for CGPA: {}, backlogs: {}, department: {}",
                cgpa,
                backlogs,
                department
        );

        if (cgpa == null || cgpa.compareTo(
                BigDecimal.ZERO) < 0) {

            throw new BusinessValidationException(
                    "CGPA must be valid"
            );
        }

        if (backlogs == null || backlogs < 0) {

            throw new BusinessValidationException(
                    "Backlogs cannot be negative"
            );
        }

        return jobDriveRepository
                .findEligibleJobDrives(
                        cgpa,
                        backlogs,
                        department,
                        DriveStatus.OPEN
                )
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // GET OPEN DRIVES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponseDTO> getOpenJobDrives() {

        log.info("Fetching currently open job drives");

        return jobDriveRepository
                .findOpenDrives(
                        DriveStatus.OPEN,
                        LocalDateTime.now()
                )
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // UPDATE DRIVE STATUS
    // =====================================================

    @Override
    public void updateDriveStatus(
            Long id,
            DriveStatus status) {

        log.info(
                "Updating job drive {} status to {}",
                id,
                status
        );

        JobDrive jobDrive =
                jobDriveRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job drive not found with ID: "
                                                + id
                                )
                        );

        if (status == null) {

            throw new BusinessValidationException(
                    "Drive status is required"
            );
        }

        jobDrive.setStatus(status);

        jobDriveRepository.save(jobDrive);

        log.info(
                "Job drive status updated successfully"
        );
    }


    // =====================================================
    // COUNT BY STATUS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public long getDriveCountByStatus(
            DriveStatus status) {

        return jobDriveRepository
                .countByStatus(status);
    }


    // =====================================================
    // DELETE JOB DRIVE
    // =====================================================

    @Override
    public void deleteJobDrive(Long id) {

        log.info(
                "Deleting job drive with ID: {}",
                id
        );

        JobDrive jobDrive =
                jobDriveRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job drive not found with ID: "
                                                + id
                                )
                        );

        /*
         * We don't physically delete a drive.
         * Existing applications may depend on it.
         *
         * Therefore mark it as CANCELLED.
         */

        jobDrive.setStatus(
                DriveStatus.CANCELLED
        );

        jobDriveRepository.save(jobDrive);

        log.info(
                "Job drive cancelled successfully"
        );
    }


    // =====================================================
    // COMPANY VALIDATION
    // =====================================================

    private Company getActiveCompany(
            Long companyId) {

        Company company =
                companyRepository.findById(companyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Company not found with ID: "
                                                + companyId
                                )
                        );

        if (!Boolean.TRUE.equals(
                company.getIsActive())) {

            throw new BusinessValidationException(
                    "Company is inactive"
            );
        }

        return company;
    }


    // =====================================================
    // DATE VALIDATION
    // =====================================================

    private void validateJobDriveDates(
            JobDriveRequestDTO requestDTO) {

        if (requestDTO.getDriveDate() == null) {

            throw new BusinessValidationException(
                    "Drive date is required"
            );
        }

        if (requestDTO.getApplicationDeadline() == null) {

            throw new BusinessValidationException(
                    "Application deadline is required"
            );
        }

        if (!requestDTO.getApplicationDeadline()
                .isBefore(requestDTO.getDriveDate())) {

            throw new BusinessValidationException(
                    "Application deadline must be before drive date"
            );
        }
    }


    // =====================================================
    // CGPA + BACKLOG VALIDATION
    // =====================================================

    private void validateCgpaAndBacklogs(
            JobDriveRequestDTO requestDTO) {

        if (requestDTO.getMinimumCgpa() != null
                && (requestDTO.getMinimumCgpa()
                .compareTo(BigDecimal.ZERO) < 0
                || requestDTO.getMinimumCgpa()
                .compareTo(BigDecimal.TEN) > 0)) {

            throw new BusinessValidationException(
                    "Minimum CGPA must be between 0 and 10"
            );
        }

        if (requestDTO.getMaximumBacklogs() != null
                && requestDTO.getMaximumBacklogs() < 0) {

            throw new BusinessValidationException(
                    "Maximum backlogs cannot be negative"
            );
        }
    }


    // =====================================================
    // BUILD RESPONSE
    // =====================================================

    private JobDriveResponseDTO buildResponse(
            JobDrive jobDrive) {

        JobDriveResponseDTO response =
                jobDriveMapper.toResponseDTO(jobDrive);

        /*
         * companyName is ignored in MapStruct.
         * So populate it here.
         */

        if (jobDrive.getCompany() != null) {

            response.setCompanyName(
                    jobDrive.getCompany().getName()
            );
        }

        return response;
    }
}