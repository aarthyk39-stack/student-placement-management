package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.entity.AuditLog;
import com.example.studentplacementmanagement.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/audits")
@RequiredArgsConstructor
@Tag(
        name = "Audit Management",
        description = "APIs for tracking and retrieving system audit logs"
)
public class AuditController {

    private final AuditService auditService;

    @Operation(
            summary = "Create audit log",
            description = "Creates a new audit log entry for a user action"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Audit log created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid audit log data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PostMapping
    public ResponseEntity<AuditLog> createAuditLog(
            @RequestParam Long userId,
            @RequestParam String action,
            @RequestParam String entityName,
            @RequestParam(required = false) String entityId) {

        AuditLog response =
                auditService.createAuditLog(
                        userId,
                        action,
                        entityName,
                        entityId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get audit logs by user",
            description = "Retrieves all audit logs created by a specific user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Audit logs retrieved successfully"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getByUserId(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @PathVariable Long userId) {

        List<AuditLog> response =
                auditService.getByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get paginated audit logs by user",
            description = "Retrieves user audit logs with pagination and sorting"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Audit logs retrieved successfully"
    )
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<AuditLog>> getByUserIdPaged(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @PathVariable Long userId,
            Pageable pageable) {

        Page<AuditLog> response =
                auditService.getByUserId(
                        userId,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get audit logs by action",
            description = "Retrieves audit logs for a specific action"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Audit logs retrieved successfully"
    )
    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getByAction(
            @Parameter(
                    description = "Audit action",
                    example = "CREATE"
            )
            @PathVariable String action) {

        List<AuditLog> response =
                auditService.getByAction(action);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get audit logs by entity",
            description = "Retrieves audit logs associated with an entity type"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Audit logs retrieved successfully"
    )
    @GetMapping("/entity/{entityName}")
    public ResponseEntity<List<AuditLog>> getByEntityName(
            @Parameter(
                    description = "Entity name",
                    example = "Student"
            )
            @PathVariable String entityName) {

        List<AuditLog> response =
                auditService.getByEntityName(entityName);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get audit logs by date range",
            description = "Retrieves audit logs created between two date-time values"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Audit logs retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date-time range"
            )
    })
    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLog>> getByDateRange(
            @Parameter(
                    description = "Start date and time",
                    example = "2026-09-01T00:00:00"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @Parameter(
                    description = "End date and time",
                    example = "2026-09-17T23:59:59"
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        List<AuditLog> response =
                auditService.getByDateRange(
                        start,
                        end
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get entity audit history",
            description = "Retrieves the complete audit history of a specific entity"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Entity history retrieved successfully"
    )
    @GetMapping("/entity/{entityName}/{entityId}")
    public ResponseEntity<List<AuditLog>> getEntityHistory(
            @Parameter(
                    description = "Entity name",
                    example = "Student"
            )
            @PathVariable String entityName,

            @Parameter(
                    description = "Entity ID",
                    example = "1"
            )
            @PathVariable String entityId) {

        List<AuditLog> response =
                auditService.getEntityHistory(
                        entityName,
                        entityId
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Count audit logs by action",
            description = "Returns the total number of audit logs for a specific action"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Audit log count retrieved successfully"
    )
    @GetMapping("/count/action/{action}")
    public ResponseEntity<Long> countByAction(
            @Parameter(
                    description = "Audit action",
                    example = "CREATE"
            )
            @PathVariable String action) {

        long count =
                auditService.countByAction(action);

        return ResponseEntity.ok(count);
    }
}