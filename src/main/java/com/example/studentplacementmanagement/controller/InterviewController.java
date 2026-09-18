package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.request.InterviewRequestDTO;
import com.example.studentplacementmanagement.dto.response.InterviewResponseDTO;
import com.example.studentplacementmanagement.enums.InterviewResult;
import com.example.studentplacementmanagement.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
@Tag(
        name = "Interview Management",
        description = "APIs for managing student placement interviews"
)
public class InterviewController {

    private final InterviewService interviewService;

    @Operation(
            summary = "Schedule an interview",
            description = "Creates and schedules a new interview"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Interview scheduled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid interview data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Application not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Interview conflict"
            )
    })
    @PostMapping
    public ResponseEntity<InterviewResponseDTO> createInterview(
            @Valid @RequestBody InterviewRequestDTO requestDTO) {

        InterviewResponseDTO response =
                interviewService.saveInterview(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get interview by ID",
            description = "Retrieves an interview using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Interview retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Interview not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<InterviewResponseDTO> getInterviewById(
            @Parameter(
                    description = "Interview ID",
                    example = "1"
            )
            @PathVariable Long id) {

        InterviewResponseDTO response =
                interviewService.getInterviewById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all interviews",
            description = "Retrieves all interviews with pagination and sorting"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Interviews retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<Page<InterviewResponseDTO>>
    getAllInterviews(Pageable pageable) {

        Page<InterviewResponseDTO> response =
                interviewService.getAllInterviews(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get interviews by application",
            description = "Retrieves all interviews associated with an application"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Interviews retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Application not found"
            )
    })
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<InterviewResponseDTO>>
    getInterviewsByApplication(
            @Parameter(
                    description = "Application ID",
                    example = "1"
            )
            @PathVariable Long applicationId) {

        List<InterviewResponseDTO> response =
                interviewService.getInterviewsByApplication(
                        applicationId
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get student interviews",
            description = "Retrieves interviews associated with a student with pagination"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Student interviews retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found"
            )
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<InterviewResponseDTO>>
    getStudentInterviews(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long studentId,
            Pageable pageable) {

        Page<InterviewResponseDTO> response =
                interviewService.getStudentInterviews(
                        studentId,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get interviews by result",
            description = "Retrieves interviews filtered by interview result"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Interviews retrieved successfully"
    )
    @GetMapping("/result/{result}")
    public ResponseEntity<List<InterviewResponseDTO>>
    getInterviewsByResult(
            @Parameter(
                    description = "Interview result",
                    example = "PASSED"
            )
            @PathVariable InterviewResult result) {

        List<InterviewResponseDTO> response =
                interviewService.getInterviewsByResult(result);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get upcoming interviews",
            description = "Retrieves interviews scheduled between the given start and end date-time"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Upcoming interviews retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date-time range"
            )
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<InterviewResponseDTO>>
    getUpcomingInterviews(
            @Parameter(
                    description = "Start date and time",
                    example = "2026-09-18T09:00:00"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @Parameter(
                    description = "End date and time",
                    example = "2026-09-18T18:00:00"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        List<InterviewResponseDTO> response =
                interviewService.getUpcomingInterviews(
                        start,
                        end
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update interview result",
            description = "Updates the result and feedback of an interview"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Interview result updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid interview result or feedback"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Interview not found"
            )
    })
    @PatchMapping("/{id}/result")
    public ResponseEntity<Void> updateInterviewResult(
            @Parameter(
                    description = "Interview ID",
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    description = "Interview result",
                    example = "PASSED"
            )
            @RequestParam InterviewResult result,

            @Parameter(
                    description = "Interview feedback",
                    example = "Candidate demonstrated strong technical skills"
            )
            @RequestParam String feedback) {

        interviewService.updateInterviewResult(
                id,
                result,
                feedback
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete interview",
            description = "Deletes an interview using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Interview deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Interview not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Interview cannot be deleted"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(
            @Parameter(
                    description = "Interview ID",
                    example = "1"
            )
            @PathVariable Long id) {

        interviewService.deleteInterview(id);

        return ResponseEntity.noContent().build();
    }
}