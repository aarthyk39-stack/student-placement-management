package com.example.studentplacementmanagement.dto.request;

import com.example.studentplacementmanagement.enums.InterviewResult;
import com.example.studentplacementmanagement.enums.InterviewType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewRequestDTO {

    private Long id;

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType;

    @NotNull(message = "Scheduled date and time is required")
    private LocalDateTime scheduledAt;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @Size(max = 100, message = "Interviewer name must not exceed 100 characters")
    private String interviewerName;

    private InterviewResult result;

    private String feedback;
}