package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.request.JobDriveRequestDTO;
import com.example.studentplacementmanagement.dto.response.JobDriveResponseDTO;
import com.example.studentplacementmanagement.enums.DriveStatus;
import com.example.studentplacementmanagement.service.JobDriveService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/job-drives")
@RequiredArgsConstructor
@Tag(
        name = "Job Drive Management",
        description = "APIs for managing company job drives"
)
public class JobDriveController {

    private final JobDriveService jobDriveService;

    @Operation(
            summary = "Create a job drive",
            description = "Creates a new job drive for a company"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Job drive created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid job drive data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Job drive already exists"
            )
    })
    @PostMapping
    public ResponseEntity<JobDriveResponseDTO> createJobDrive(
            @Valid @RequestBody JobDriveRequestDTO requestDTO) {

        JobDriveResponseDTO response =
                jobDriveService.saveJobDrive(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get job drive by ID",
            description = "Retrieves a job drive using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Job drive retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Job drive not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobDriveResponseDTO> getJobDriveById(
            @Parameter(
                    description = "Job drive ID",
                    example = "1"
            )
            @PathVariable Long id) {

        JobDriveResponseDTO response =
                jobDriveService.getJobDriveById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all job drives",
            description = "Retrieves all job drives with pagination and sorting"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Job drives retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<Page<JobDriveResponseDTO>> getAllJobDrives(
            Pageable pageable) {

        Page<JobDriveResponseDTO> response =
                jobDriveService.getAllJobDrives(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search job drives",
            description = "Searches job drives using a keyword with pagination"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search parameter"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<Page<JobDriveResponseDTO>> searchJobDrives(
            @Parameter(
                    description = "Search keyword",
                    example = "Java Developer"
            )
            @RequestParam String keyword,
            Pageable pageable) {

        Page<JobDriveResponseDTO> response =
                jobDriveService.searchJobDrives(
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get job drives by company",
            description = "Retrieves all job drives belonging to a company"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Job drives retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found"
            )
    })
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobDriveResponseDTO>>
    getJobDrivesByCompany(
            @Parameter(
                    description = "Company ID",
                    example = "1"
            )
            @PathVariable Long companyId) {

        List<JobDriveResponseDTO> response =
                jobDriveService.getJobDrivesByCompany(
                        companyId
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get job drives by status",
            description = "Retrieves job drives filtered by drive status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Job drives retrieved successfully"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobDriveResponseDTO>>
    getJobDrivesByStatus(
            @Parameter(
                    description = "Drive status",
                    example = "OPEN"
            )
            @PathVariable DriveStatus status) {

        List<JobDriveResponseDTO> response =
                jobDriveService.getJobDrivesByStatus(
                        status
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get eligible job drives",
            description = "Retrieves job drives based on CGPA, backlogs and department eligibility"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eligible job drives retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid eligibility criteria"
            )
    })
    @GetMapping("/eligible")
    public ResponseEntity<List<JobDriveResponseDTO>>
    getEligibleJobDrives(
            @Parameter(
                    description = "Student CGPA",
                    example = "8.2"
            )
            @RequestParam BigDecimal cgpa,

            @Parameter(
                    description = "Number of backlogs",
                    example = "0"
            )
            @RequestParam Integer backlogs,

            @Parameter(
                    description = "Student department",
                    example = "Computer Science"
            )
            @RequestParam String department) {

        List<JobDriveResponseDTO> response =
                jobDriveService.getEligibleJobDrives(
                        cgpa,
                        backlogs,
                        department
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get open job drives",
            description = "Retrieves all currently open job drives"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Open job drives retrieved successfully"
    )
    @GetMapping("/open")
    public ResponseEntity<List<JobDriveResponseDTO>>
    getOpenJobDrives() {

        List<JobDriveResponseDTO> response =
                jobDriveService.getOpenJobDrives();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update job drive status",
            description = "Updates the status of an existing job drive"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Job drive status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid drive status"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Job drive not found"
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateDriveStatus(
            @Parameter(
                    description = "Job drive ID",
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    description = "New drive status",
                    example = "CLOSED"
            )
            @RequestParam DriveStatus status) {

        jobDriveService.updateDriveStatus(
                id,
                status
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get job drive count by status",
            description = "Returns the number of job drives for a specific status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Job drive count retrieved successfully"
    )
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getDriveCountByStatus(
            @Parameter(
                    description = "Drive status",
                    example = "OPEN"
            )
            @PathVariable DriveStatus status) {

        long count =
                jobDriveService.getDriveCountByStatus(
                        status
                );

        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Delete job drive",
            description = "Deletes a job drive using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Job drive deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Job drive not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Job drive cannot be deleted because it is referenced by other resources"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobDrive(
            @Parameter(
                    description = "Job drive ID",
                    example = "1"
            )
            @PathVariable Long id) {

        jobDriveService.deleteJobDrive(id);

        return ResponseEntity.noContent().build();
    }
}