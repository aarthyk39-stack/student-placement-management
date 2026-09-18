package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.InterviewRequestDTO;
import com.example.studentplacementmanagement.dto.response.InterviewResponseDTO;
import com.example.studentplacementmanagement.entity.Application;
import com.example.studentplacementmanagement.entity.Interview;
import com.example.studentplacementmanagement.enums.ApplicationStatus;
import com.example.studentplacementmanagement.enums.InterviewResult;
import com.example.studentplacementmanagement.exception.BusinessValidationException;
import com.example.studentplacementmanagement.exception.DuplicateResourceException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.mapper.InterviewMapper;
import com.example.studentplacementmanagement.repository.ApplicationRepository;
import com.example.studentplacementmanagement.repository.InterviewRepository;
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
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;

    private final ApplicationRepository applicationRepository;

    private final InterviewMapper interviewMapper;


    // =====================================================
    // CREATE + UPDATE INTERVIEW
    // =====================================================

    @Override
    public InterviewResponseDTO saveInterview(
            InterviewRequestDTO requestDTO) {

        Interview interview;

        // =================================================
        // CREATE
        // =================================================

        if (requestDTO.getId() == null) {

            log.info(
                    "Creating interview for application ID: {}",
                    requestDTO.getApplicationId()
            );

            // ---------------------------------------------
            // GET APPLICATION
            // ---------------------------------------------

            Application application =
                    getApplication(
                            requestDTO.getApplicationId()
                    );

            // ---------------------------------------------
            // VALIDATE APPLICATION
            // ---------------------------------------------

            validateApplicationForInterview(
                    application
            );

            // ---------------------------------------------
            // VALIDATE SCHEDULE
            // ---------------------------------------------

            validateInterviewTime(
                    requestDTO.getScheduledAt()
            );

            // ---------------------------------------------
            // CREATE ENTITY
            // ---------------------------------------------

            interview =
                    interviewMapper.toEntity(requestDTO);

            // Set actual application
            interview.setApplication(application);

            // Default result
            if (interview.getResult() == null) {

                interview.setResult(
                        InterviewResult.PENDING
                );
            }

            // ---------------------------------------------
            // UPDATE APPLICATION STATUS
            // ---------------------------------------------

            application.setStatus(
                    ApplicationStatus.INTERVIEW_SCHEDULED
            );

            applicationRepository.save(application);
        }

        // =================================================
        // UPDATE
        // =================================================

        else {

            log.info(
                    "Updating interview with ID: {}",
                    requestDTO.getId()
            );

            // Find existing interview
            interview =
                    interviewRepository.findById(
                            requestDTO.getId()
                    ).orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Interview not found with ID: "
                                            + requestDTO.getId()
                            )
                    );

            // ---------------------------------------------
            // APPLICATION CANNOT BE CHANGED
            // ---------------------------------------------

            if (requestDTO.getApplicationId() == null) {

                throw new BusinessValidationException(
                        "Application ID is required"
                );
            }

            if (!interview.getApplication()
                    .getId()
                    .equals(requestDTO.getApplicationId())) {

                throw new BusinessValidationException(
                        "Application cannot be changed for an existing interview"
                );
            }

            // ---------------------------------------------
            // VALIDATE TIME
            // ---------------------------------------------

            validateInterviewTimeForUpdate(
                    requestDTO.getScheduledAt(),
                    interview
            );

            // ---------------------------------------------
            // UPDATE FIELDS
            // ---------------------------------------------

            interview.setInterviewType(
                    requestDTO.getInterviewType()
            );

            interview.setScheduledAt(
                    requestDTO.getScheduledAt()
            );

            interview.setLocation(
                    requestDTO.getLocation()
            );

            interview.setInterviewerName(
                    requestDTO.getInterviewerName()
            );

            /*
             * Result is handled through
             * updateInterviewResult().
             *
             * This prevents accidentally changing
             * interview result during normal update.
             */

            if (requestDTO.getFeedback() != null) {

                interview.setFeedback(
                        requestDTO.getFeedback()
                );
            }
        }

        // =================================================
        // SAVE
        // =================================================

        Interview savedInterview =
                interviewRepository.save(interview);

        log.info(
                "Interview saved successfully with ID: {}",
                savedInterview.getId()
        );

        return buildResponse(savedInterview);
    }


    // =====================================================
    // GET BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public InterviewResponseDTO getInterviewById(
            Long id) {

        log.info(
                "Fetching interview with ID: {}",
                id
        );

        Interview interview =
                interviewRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with ID: "
                                                + id
                                )
                        );

        return buildResponse(interview);
    }


    // =====================================================
    // GET ALL
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<InterviewResponseDTO> getAllInterviews(
            Pageable pageable) {

        log.info("Fetching all interviews");

        return interviewRepository
                .findAll(pageable)
                .map(this::buildResponse);
    }


    // =====================================================
    // GET BY APPLICATION
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getInterviewsByApplication(
            Long applicationId) {

        log.info(
                "Fetching interviews for application ID: {}",
                applicationId
        );

        if (!applicationRepository.existsById(
                applicationId)) {

            throw new ResourceNotFoundException(
                    "Application not found with ID: "
                            + applicationId
            );
        }

        return interviewRepository
                .findByApplicationId(applicationId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // GET STUDENT INTERVIEWS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<InterviewResponseDTO> getStudentInterviews(
            Long studentId,
            Pageable pageable) {

        log.info(
                "Fetching interviews for student ID: {}",
                studentId
        );

        return interviewRepository
                .findByApplicationStudentId(
                        studentId,
                        pageable
                )
                .map(this::buildResponse);
    }


    // =====================================================
    // GET BY RESULT
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getInterviewsByResult(
            InterviewResult result) {

        log.info(
                "Fetching interviews with result: {}",
                result
        );

        return interviewRepository
                .findByResult(result)
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // UPCOMING INTERVIEWS
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getUpcomingInterviews(
            LocalDateTime start,
            LocalDateTime end) {

        log.info(
                "Fetching interviews between {} and {}",
                start,
                end
        );

        if (start == null || end == null) {

            throw new BusinessValidationException(
                    "Start and end time are required"
            );
        }

        if (end.isBefore(start)) {

            throw new BusinessValidationException(
                    "End time must be after start time"
            );
        }

        return interviewRepository
                .findUpcomingInterviews(start, end)
                .stream()
                .map(this::buildResponse)
                .toList();
    }


    // =====================================================
    // UPDATE INTERVIEW RESULT
    // =====================================================

    @Override
    public void updateInterviewResult(
            Long id,
            InterviewResult result,
            String feedback) {

        log.info(
                "Updating interview {} result to {}",
                id,
                result
        );

        if (result == null) {

            throw new BusinessValidationException(
                    "Interview result is required"
            );
        }

        Interview interview =
                interviewRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with ID: "
                                                + id
                                )
                        );

        // ---------------------------------------------
        // PENDING
        // ---------------------------------------------

        if (result == InterviewResult.PENDING) {

            throw new BusinessValidationException(
                    "Use a final result: PASSED, FAILED or ABSENT"
            );
        }

        // ---------------------------------------------
        // INTERVIEW TIME CHECK
        // ---------------------------------------------

        if (interview.getScheduledAt() != null
                && LocalDateTime.now()
                .isBefore(interview.getScheduledAt())) {

            throw new BusinessValidationException(
                    "Interview result cannot be updated before interview time"
            );
        }

        // ---------------------------------------------
        // UPDATE RESULT
        // ---------------------------------------------

        interview.setResult(result);

        interview.setFeedback(feedback);

        interviewRepository.save(interview);

        // ---------------------------------------------
        // UPDATE APPLICATION STATUS
        // ---------------------------------------------

        Application application =
                interview.getApplication();

        if (result == InterviewResult.PASSED) {

            application.setStatus(
                    ApplicationStatus.SELECTED
            );

        } else if (result == InterviewResult.FAILED
                || result == InterviewResult.ABSENT) {

            application.setStatus(
                    ApplicationStatus.REJECTED
            );
        }

        applicationRepository.save(application);

        log.info(
                "Interview result and application status updated successfully"
        );
    }


    // =====================================================
    // DELETE INTERVIEW
    // =====================================================

    @Override
    public void deleteInterview(Long id) {

        log.info(
                "Deleting interview with ID: {}",
                id
        );

        Interview interview =
                interviewRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with ID: "
                                                + id
                                )
                        );

        // Don't delete completed interview history
        if (interview.getResult() !=
                InterviewResult.PENDING) {

            throw new BusinessValidationException(
                    "Completed interview cannot be deleted"
            );
        }

        interviewRepository.delete(interview);

        log.info(
                "Interview deleted successfully"
        );
    }


    // =====================================================
    // GET APPLICATION
    // =====================================================

    private Application getApplication(
            Long applicationId) {

        return applicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with ID: "
                                        + applicationId
                        )
                );
    }


    // =====================================================
    // VALIDATE APPLICATION
    // =====================================================

    private void validateApplicationForInterview(
            Application application) {

        ApplicationStatus status =
                application.getStatus();

        if (status != ApplicationStatus.SHORTLISTED
                && status != ApplicationStatus.INTERVIEW_SCHEDULED) {

            throw new BusinessValidationException(
                    "Interview can be scheduled only for shortlisted application"
            );
        }

        if (status == ApplicationStatus.REJECTED
                || status == ApplicationStatus.WITHDRAWN
                || status == ApplicationStatus.PLACED) {

            throw new BusinessValidationException(
                    "Interview cannot be scheduled for this application"
            );
        }
    }


    // =====================================================
    // VALIDATE INTERVIEW TIME
    // =====================================================

    private void validateInterviewTime(
            LocalDateTime scheduledAt) {

        if (scheduledAt == null) {

            throw new BusinessValidationException(
                    "Interview scheduled time is required"
            );
        }

        if (!scheduledAt.isAfter(
                LocalDateTime.now())) {

            throw new BusinessValidationException(
                    "Interview must be scheduled for a future date and time"
            );
        }
    }


    // =====================================================
    // VALIDATE UPDATE TIME
    // =====================================================

    private void validateInterviewTimeForUpdate(
            LocalDateTime scheduledAt,
            Interview existingInterview) {

        if (scheduledAt == null) {

            throw new BusinessValidationException(
                    "Interview scheduled time is required"
            );
        }

        /*
         * During update, allow keeping an already scheduled
         * interview time, but don't allow changing it to
         * a past time.
         */

        if (!scheduledAt.isAfter(
                LocalDateTime.now())) {

            throw new BusinessValidationException(
                    "Interview must be scheduled for a future date and time"
            );
        }
    }


    // =====================================================
    // BUILD RESPONSE
    // =====================================================

    private InterviewResponseDTO buildResponse(
            Interview interview) {

        InterviewResponseDTO response =
                interviewMapper.toResponseDTO(
                        interview
                );

        if (interview.getApplication() != null) {

            Application application =
                    interview.getApplication();

            // Student name
            if (application.getStudent() != null) {

                response.setStudentName(
                        application.getStudent()
                                .getName()
                );
            }

            // Job title
            if (application.getJobDrive() != null) {

                response.setJobTitle(
                        application.getJobDrive()
                                .getJobTitle()
                );
            }
        }

        return response;
    }
}