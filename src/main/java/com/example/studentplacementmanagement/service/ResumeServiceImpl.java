package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.response.ResumeResponseDTO;
import com.example.studentplacementmanagement.entity.Resume;
import com.example.studentplacementmanagement.entity.Student;
import com.example.studentplacementmanagement.exception.BusinessValidationException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.mapper.ResumeMapper;
import com.example.studentplacementmanagement.repository.ResumeRepository;
import com.example.studentplacementmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;

    private final StudentRepository studentRepository;

    private final ResumeMapper resumeMapper;

    @Value("${file.upload-dir:uploads/resumes}")
    private String uploadDirectory;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            );

    @Override
    public ResumeResponseDTO uploadResume(
            Long studentId,
            MultipartFile file) {

        log.info(
                "Uploading resume for student ID: {}",
                studentId
        );

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with ID: "
                                        + studentId
                        )
                );

        validateFile(file);

        try {
            Path uploadPath = Paths.get(
                    uploadDirectory
            ).toAbsolutePath().normalize();

            Files.createDirectories(uploadPath);

            String originalFileName =
                    StringUtils.cleanPath(
                            file.getOriginalFilename()
                    );

            String extension =
                    getFileExtension(originalFileName);

            String storedFileName =
                    UUID.randomUUID()
                            + extension;

            Path targetPath =
                    uploadPath.resolve(storedFileName)
                            .normalize();

            if (!targetPath.startsWith(uploadPath)) {
                throw new BusinessValidationException(
                        "Invalid file path"
                );
            }

            Resume resume =
                    resumeRepository
                            .findByStudentId(studentId)
                            .orElse(null);

            if (resume == null) {

                resume = new Resume();

                resume.setStudent(student);

            } else {

                deletePhysicalFile(
                        resume.getFilePath()
                );
            }

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            resume.setOriginalFileName(
                    originalFileName
            );

            resume.setStoredFileName(
                    storedFileName
            );

            resume.setFilePath(
                    targetPath.toString()
            );

            resume.setContentType(
                    file.getContentType()
            );

            resume.setFileSize(
                    file.getSize()
            );

            Resume savedResume =
                    resumeRepository.save(resume);

            log.info(
                    "Resume uploaded successfully with ID: {}",
                    savedResume.getId()
            );

            return resumeMapper.toResponseDTO(
                    savedResume
            );

        } catch (IOException exception) {

            log.error(
                    "Failed to upload resume for student ID: {}",
                    studentId,
                    exception
            );

            throw new BusinessValidationException(
                    "Failed to upload resume"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeResponseDTO getResumeByStudentId(
            Long studentId) {

        log.info(
                "Fetching resume for student ID: {}",
                studentId
        );

        if (!studentRepository.existsById(studentId)) {

            throw new ResourceNotFoundException(
                    "Student not found with ID: "
                            + studentId
            );
        }

        Resume resume =
                resumeRepository
                        .findByStudentId(studentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resume not found for student ID: "
                                                + studentId
                                )
                        );

        return resumeMapper.toResponseDTO(resume);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadResume(
            Long studentId) {

        log.info(
                "Downloading resume for student ID: {}",
                studentId
        );

        Resume resume =
                resumeRepository
                        .findByStudentId(studentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resume not found for student ID: "
                                                + studentId
                                )
                        );

        try {

            Path filePath =
                    Paths.get(
                            resume.getFilePath()
                    ).toAbsolutePath().normalize();

            if (!Files.exists(filePath)) {

                throw new ResourceNotFoundException(
                        "Resume file not found"
                );
            }

            return Files.readAllBytes(filePath);

        } catch (IOException exception) {

            log.error(
                    "Failed to download resume for student ID: {}",
                    studentId,
                    exception
            );

            throw new BusinessValidationException(
                    "Failed to download resume"
            );
        }
    }

    @Override
    public void deleteResume(
            Long studentId) {

        log.info(
                "Deleting resume for student ID: {}",
                studentId
        );

        Resume resume =
                resumeRepository
                        .findByStudentId(studentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resume not found for student ID: "
                                                + studentId
                                )
                        );

        deletePhysicalFile(
                resume.getFilePath()
        );

        resumeRepository.delete(resume);

        log.info(
                "Resume deleted successfully for student ID: {}",
                studentId
        );
    }

    private void validateFile(
            MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new BusinessValidationException(
                    "Resume file is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new BusinessValidationException(
                    "Resume file size must not exceed 5 MB"
            );
        }

        String originalFileName =
                file.getOriginalFilename();

        if (!StringUtils.hasText(
                originalFileName)) {

            throw new BusinessValidationException(
                    "Invalid file name"
            );
        }

        String extension =
                getFileExtension(
                        originalFileName
                ).toLowerCase();

        if (!extension.equals(".pdf")
                && !extension.equals(".doc")
                && !extension.equals(".docx")) {

            throw new BusinessValidationException(
                    "Only PDF, DOC and DOCX files are allowed"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES
                .contains(contentType)) {

            throw new BusinessValidationException(
                    "Invalid resume file type"
            );
        }
    }

    private String getFileExtension(
            String fileName) {

        int index =
                fileName.lastIndexOf(".");

        if (index == -1) {
            return "";
        }

        return fileName.substring(index);
    }

    private void deletePhysicalFile(
            String filePath) {

        if (!StringUtils.hasText(filePath)) {
            return;
        }

        try {

            Path path =
                    Paths.get(filePath)
                            .toAbsolutePath()
                            .normalize();

            Files.deleteIfExists(path);

        } catch (IOException exception) {

            log.warn(
                    "Unable to delete old resume file: {}",
                    filePath,
                    exception
            );
        }
    }
}