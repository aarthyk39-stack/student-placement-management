package com.example.studentplacementmanagement.dto.response;

import com.example.studentplacementmanagement.enums.InterviewResult;
import com.example.studentplacementmanagement.enums.InterviewType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewResponseDTO {

    private Long id;

    private Long applicationId;

    private String studentName;

    private String jobTitle;

    private InterviewType interviewType;

    private LocalDateTime scheduledAt;

    private String location;

    private String interviewerName;

    private InterviewResult result;

    private String feedback;
}