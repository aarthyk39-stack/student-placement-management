package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.request.OfferRequestDTO;
import com.example.studentplacementmanagement.dto.response.OfferResponseDTO;
import com.example.studentplacementmanagement.enums.OfferStatus;
import com.example.studentplacementmanagement.service.OfferService;
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
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
@Tag(
        name = "Offer Management",
        description = "APIs for managing student placement offers"
)
public class OfferController {

    private final OfferService offerService;

    @Operation(
            summary = "Create an offer",
            description = "Creates a new placement offer for a student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Offer created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid offer data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Application or student not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Offer already exists"
            )
    })
    @PostMapping
    public ResponseEntity<OfferResponseDTO> createOffer(
            @Valid @RequestBody OfferRequestDTO requestDTO) {

        OfferResponseDTO response =
                offerService.saveOffer(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get offer by ID",
            description = "Retrieves an offer using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<OfferResponseDTO> getOfferById(
            @Parameter(
                    description = "Offer ID",
                    example = "1"
            )
            @PathVariable Long id) {

        OfferResponseDTO response =
                offerService.getOfferById(id);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all offers",
            description = "Retrieves all placement offers with pagination and sorting"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Offers retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<Page<OfferResponseDTO>> getAllOffers(
            Pageable pageable) {

        Page<OfferResponseDTO> response =
                offerService.getAllOffers(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get offers by student",
            description = "Retrieves all placement offers received by a student"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offers retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Student not found"
            )
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<OfferResponseDTO>>
    getOffersByStudent(
            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @PathVariable Long studentId) {

        List<OfferResponseDTO> response =
                offerService.getOffersByStudent(studentId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get offers by status",
            description = "Retrieves placement offers filtered by offer status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Offers retrieved successfully"
    )
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OfferResponseDTO>>
    getOffersByStatus(
            @Parameter(
                    description = "Offer status",
                    example = "ACCEPTED"
            )
            @PathVariable OfferStatus status) {

        List<OfferResponseDTO> response =
                offerService.getOffersByStatus(status);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update offer status",
            description = "Updates the status of an existing placement offer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Offer status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid offer status"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found"
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOfferStatus(
            @Parameter(
                    description = "Offer ID",
                    example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                    description = "New offer status",
                    example = "ACCEPTED"
            )
            @RequestParam OfferStatus status) {

        offerService.updateOfferStatus(
                id,
                status
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get offer count by status",
            description = "Returns the number of offers for a specific status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Offer count retrieved successfully"
    )
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getOfferCountByStatus(
            @Parameter(
                    description = "Offer status",
                    example = "ACCEPTED"
            )
            @PathVariable OfferStatus status) {

        long count =
                offerService.getOfferCountByStatus(status);

        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Delete offer",
            description = "Deletes an offer using its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Offer deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Offer cannot be deleted"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(
            @Parameter(
                    description = "Offer ID",
                    example = "1"
            )
            @PathVariable Long id) {

        offerService.deleteOffer(id);

        return ResponseEntity.noContent().build();
    }
}