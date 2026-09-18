package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.InterviewRequestDTO;
import com.example.studentplacementmanagement.dto.response.InterviewResponseDTO;
import com.example.studentplacementmanagement.enums.InterviewResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewService {

    InterviewResponseDTO saveInterview(
            InterviewRequestDTO requestDTO
    );

    InterviewResponseDTO getInterviewById(
            Long id
    );

    Page<InterviewResponseDTO> getAllInterviews(
            Pageable pageable
    );

    List<InterviewResponseDTO> getInterviewsByApplication(
            Long applicationId
    );

    Page<InterviewResponseDTO> getStudentInterviews(
            Long studentId,
            Pageable pageable
    );

    List<InterviewResponseDTO> getInterviewsByResult(
            InterviewResult result
    );

    List<InterviewResponseDTO> getUpcomingInterviews(
            LocalDateTime start,
            LocalDateTime end
    );

    void updateInterviewResult(
            Long id,
            InterviewResult result,
            String feedback
    );

    void deleteInterview(Long id);
}