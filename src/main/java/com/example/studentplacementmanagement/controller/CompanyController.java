package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.request.CompanyRequestDTO;
import com.example.studentplacementmanagement.dto.response.CompanyResponseDTO;
import com.example.studentplacementmanagement.service.CompanyService;
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
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(
        name = "Company Management",
        description = "APIs for managing companies"
)
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
            summary = "Create a company",
            description = "Creates a new company in the placement management system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Company created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid company data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Company already exists"
            )
    })
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(
            @Valid @RequestBody CompanyRequestDTO requestDTO) {

        CompanyResponseDTO response =
                companyService.saveCompany(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get company by ID",
            description = "Retrieves a company using the company ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Company retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(
            @Parameter(
                    description = "Company ID",
                    example = "1"
            )
            @PathVariable Long id) {

        CompanyResponseDTO response =
                companyService.getCompanyById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all companies",
            description = "Retrieves companies with pagination and sorting"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Companies retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<Page<CompanyResponseDTO>> getAllCompanies(
            Pageable pageable) {

        Page<CompanyResponseDTO> response =
                companyService.getAllCompanies(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search companies",
            description = "Searches companies using a keyword with pagination"
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
    public ResponseEntity<Page<CompanyResponseDTO>> searchCompanies(
            @Parameter(
                    description = "Search keyword",
                    example = "Google"
            )
            @RequestParam String keyword,
            Pageable pageable) {

        Page<CompanyResponseDTO> response =
                companyService.searchCompanies(
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get companies by industry",
            description = "Retrieves companies belonging to a specific industry"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Companies retrieved successfully"
    )
    @GetMapping("/industry/{industry}")
    public ResponseEntity<List<CompanyResponseDTO>>
    getCompaniesByIndustry(
            @Parameter(
                    description = "Industry name",
                    example = "Information Technology"
            )
            @PathVariable String industry) {

        List<CompanyResponseDTO> response =
                companyService.getCompaniesByIndustry(
                        industry
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get active companies",
            description = "Retrieves all currently active companies"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Active companies retrieved successfully"
    )
    @GetMapping("/active")
    public ResponseEntity<List<CompanyResponseDTO>>
    getActiveCompanies() {

        List<CompanyResponseDTO> response =
                companyService.getActiveCompanies();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get active company count",
            description = "Returns the total number of active companies"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Active company count retrieved successfully"
    )
    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveCompanyCount() {

        long count =
                companyService.getActiveCompanyCount();

        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Delete company",
            description = "Deletes a company using the company ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Company deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Company cannot be deleted because it is referenced by other resources"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(
            @Parameter(
                    description = "Company ID",
                    example = "1"
            )
            @PathVariable Long id) {

        companyService.deleteCompany(id);

        return ResponseEntity.noContent().build();
    }
}