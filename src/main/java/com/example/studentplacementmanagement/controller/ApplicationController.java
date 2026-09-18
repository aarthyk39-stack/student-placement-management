package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.request.ApplicationRequestDTO;
import com.example.studentplacementmanagement.dto.response.ApplicationResponseDTO;
import com.example.studentplacementmanagement.enums.ApplicationStatus;
import com.example.studentplacementmanagement.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Tag(
        name = "Application Management",
        description = "APIs for managing student job applications"
)
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(
            summary = "Create an application",
            description = "Creates a new job application for a student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Application created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid application data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student or job drive not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Application already exists"
            )
    })
    @PostMapping
    public ResponseEntity<ApplicationResponseDTO> createApplication(
            @Valid @RequestBody ApplicationRequestDTO requestDTO) {

        ApplicationResponseDTO response =
                applicationService.saveApplication(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get application by ID",
            description = "Retrieves an application using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Application retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Application not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> getApplicationById(
            @Parameter(
                    description = "Application ID",
                    example = "1"
            )
            @PathVariable Long id) {

        ApplicationResponseDTO response =
                applicationService.getApplicationById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all applications",
            description = "Retrieves all applications with pagination and sorting"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Applications retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<Page<ApplicationResponseDTO>>
    getAllApplications(Pageable pageable) {

        Page<ApplicationResponseDTO> response =
                applicationService.getAllApplications(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get applications by student",
            description = "Retrieves all applications submitted by a specific student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Applications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found"
            )
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<ApplicationResponseDTO>>
    getApplicationsByStudent(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long studentId,
            Pageable pageable) {

        Page<ApplicationResponseDTO> response =
                applicationService.getApplicationsByStudent(
                        studentId,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get applications by job drive",
            description = "Retrieves all applications submitted for a specific job drive"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Applications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Job drive not found"
            )
    })
    @GetMapping("/job-drive/{jobDriveId}")
    public ResponseEntity<Page<ApplicationResponseDTO>>
    getApplicationsByJobDrive(
            @Parameter(
                    description = "Job drive ID",
                    example = "1"
            )
            @PathVariable Long jobDriveId,
            Pageable pageable) {

        Page<ApplicationResponseDTO> response =
                applicationService.getApplicationsByJobDrive(
                        jobDriveId,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get applications by status",
            description = "Retrieves applications filtered by application status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Applications retrieved successfully"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApplicationResponseDTO>>
    getApplicationsByStatus(
            @Parameter(
                    description = "Application status",
                    example = "SHORTLISTED"
            )
            @PathVariable ApplicationStatus status) {

        List<ApplicationResponseDTO> response =
                applicationService.getApplicationsByStatus(status);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get student placement history",
            description = "Retrieves the placement application history of a student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Placement history retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found"
            )
    })
    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<List<ApplicationResponseDTO>>
    getStudentPlacementHistory(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long studentId) {

        List<ApplicationResponseDTO> response =
                applicationService.getStudentPlacementHistory(
                        studentId
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update application status",
            description = "Updates the status of an existing application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Application status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid application status"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Application not found"
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateApplicationStatus(
            @Parameter(
                    description = "Application ID",
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    description = "New application status",
                    example = "SHORTLISTED"
            )
            @RequestParam ApplicationStatus status) {

        applicationService.updateApplicationStatus(
                id,
                status
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get application count by status",
            description = "Returns the number of applications for a specific status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Application count retrieved successfully"
    )
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getApplicationCountByStatus(
            @Parameter(
                    description = "Application status",
                    example = "SHORTLISTED"
            )
            @PathVariable ApplicationStatus status) {

        long count =
                applicationService.getApplicationCountByStatus(
                        status
                );

        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Get application count by job drive",
            description = "Returns the number of applications submitted for a job drive"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Application count retrieved successfully"
    )
    @GetMapping("/count/job-drive/{jobDriveId}")
    public ResponseEntity<Long> getApplicationCountByJobDrive(
            @Parameter(
                    description = "Job drive ID",
                    example = "1"
            )
            @PathVariable Long jobDriveId) {

        long count =
                applicationService.getApplicationCountByJobDrive(
                        jobDriveId
                );

        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Delete application",
            description = "Deletes an application using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Application deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Application not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Application cannot be deleted"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @Parameter(
                    description = "Application ID",
                    example = "1"
            )
            @PathVariable Long id) {

        applicationService.deleteApplication(id);

        return ResponseEntity.noContent().build();
    }
}