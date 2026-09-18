package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.response.ResumeResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    ResumeResponseDTO uploadResume(
            Long studentId,
            MultipartFile file
    );

    ResumeResponseDTO getResumeByStudentId(
            Long studentId
    );

    byte[] downloadResume(
            Long studentId
    );

    void deleteResume(
            Long studentId
    );
}