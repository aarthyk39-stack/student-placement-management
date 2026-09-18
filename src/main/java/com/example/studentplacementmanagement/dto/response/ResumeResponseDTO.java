package com.example.studentplacementmanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResumeResponseDTO {

    private Long id;

    private Long studentId;

    private String originalFileName;

    private String contentType;

    private Long fileSize;

    private LocalDateTime uploadedAt;
}