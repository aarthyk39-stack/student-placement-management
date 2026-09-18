package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.request.StudentRequestDTO;
import com.example.studentplacementmanagement.dto.response.StudentResponseDTO;
import com.example.studentplacementmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(
        name = "Student Management",
        description = "APIs for managing students"
)
public class StudentController {

    private final StudentService studentService;

    @Operation(
            summary = "Create a student",
            description = "Creates a new student in the placement management system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Student created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = StudentResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid student data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Student already exists"
            )
    })
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        StudentResponseDTO response =
                studentService.saveStudent(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get student by ID",
            description = "Retrieves a student using the student ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Student retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long id) {

        StudentResponseDTO response =
                studentService.getStudentById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all students",
            description = "Retrieves students with pagination and sorting"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Students retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<Page<StudentResponseDTO>> getAllStudents(
            @Parameter(
                    description = "Pagination and sorting parameters"
            )
            Pageable pageable) {

        Page<StudentResponseDTO> response =
                studentService.getAllStudents(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search students",
            description = "Searches students using a keyword with pagination"
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
    public ResponseEntity<Page<StudentResponseDTO>> searchStudents(
            @Parameter(
                    description = "Search keyword",
                    example = "Aarthy"
            )
            @RequestParam String keyword,
            Pageable pageable) {

        Page<StudentResponseDTO> response =
                studentService.searchStudents(
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get students by department",
            description = "Retrieves students belonging to a specific department"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Students retrieved successfully"
            )
    })
    @GetMapping("/department/{department}")
    public ResponseEntity<List<StudentResponseDTO>>
    getStudentsByDepartment(
            @Parameter(
                    description = "Department name",
                    example = "Computer Science"
            )
            @PathVariable String department) {

        List<StudentResponseDTO> response =
                studentService.getStudentsByDepartment(
                        department
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get eligible students",
            description = "Retrieves students who satisfy CGPA and backlog eligibility criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eligible students retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid eligibility criteria"
            )
    })
    @GetMapping("/eligible")
    public ResponseEntity<List<StudentResponseDTO>>
    getEligibleStudents(
            @Parameter(
                    description = "Minimum CGPA required",
                    example = "7.5"
            )
            @RequestParam BigDecimal minimumCgpa,

            @Parameter(
                    description = "Maximum number of backlogs allowed",
                    example = "2"
            )
            @RequestParam Integer maximumBacklogs) {

        List<StudentResponseDTO> response =
                studentService.getEligibleStudents(
                        minimumCgpa,
                        maximumBacklogs
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get placed student count",
            description = "Returns the total number of placed students"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Placed student count retrieved successfully"
    )
    @GetMapping("/count/placed")
    public ResponseEntity<Long> getPlacedStudentCount() {

        long count =
                studentService.getPlacedStudentCount();

        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Get unplaced student count",
            description = "Returns the total number of unplaced students"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Unplaced student count retrieved successfully"
    )
    @GetMapping("/count/unplaced")
    public ResponseEntity<Long> getUnplacedStudentCount() {

        long count =
                studentService.getUnplacedStudentCount();

        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Delete student",
            description = "Deletes a student using the student ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Student deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.noContent().build();
    }
}