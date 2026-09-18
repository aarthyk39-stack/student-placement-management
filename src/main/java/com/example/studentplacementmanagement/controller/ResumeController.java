package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.response.ResumeResponseDTO;
import com.example.studentplacementmanagement.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
@Tag(
        name = "Resume Management",
        description = "APIs for uploading, downloading and managing student resumes"
)
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(
            summary = "Upload student resume",
            description = "Uploads or replaces the resume of a student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Resume uploaded successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or student ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found"
            )
    })
    @PostMapping(
            value = "/student/{studentId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResumeResponseDTO> uploadResume(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long studentId,

            @Parameter(
                    description = "Resume file in PDF, DOC or DOCX format"
            )
            @RequestParam("file") MultipartFile file) {

        ResumeResponseDTO response =
                resumeService.uploadResume(
                        studentId,
                        file
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get student resume",
            description = "Retrieves resume metadata for a student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resume retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resume or student not found"
            )
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ResumeResponseDTO> getResumeByStudentId(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long studentId) {

        ResumeResponseDTO response =
                resumeService.getResumeByStudentId(
                        studentId
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Download student resume",
            description = "Downloads the uploaded resume file of a student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resume downloaded successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resume not found"
            )
    })
    @GetMapping("/student/{studentId}/download")
    public ResponseEntity<byte[]> downloadResume(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long studentId) {

        byte[] file =
                resumeService.downloadResume(studentId);

        ResumeResponseDTO resume =
                resumeService.getResumeByStudentId(studentId);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        resume.getContentType()
                )
        );

        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(
                                resume.getOriginalFileName(),
                                StandardCharsets.UTF_8
                        )
                        .build()
        );

        headers.setContentLength(file.length);

        return new ResponseEntity<>(
                file,
                headers,
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Delete student resume",
            description = "Deletes the resume of a student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Resume deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resume not found"
            )
    })
    @DeleteMapping("/student/{studentId}")
    public ResponseEntity<Void> deleteResume(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long studentId) {

        resumeService.deleteResume(studentId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}