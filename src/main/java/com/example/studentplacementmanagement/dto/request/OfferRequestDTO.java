package com.example.studentplacementmanagement.dto.request;

import com.example.studentplacementmanagement.enums.OfferStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OfferRequestDTO {

    private Long id;

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotBlank(message = "Offer letter number is required")
    private String offerLetterNumber;

    @NotNull(message = "Salary package is required")
    @DecimalMin(value = "0.0", message = "Salary package cannot be negative")
    private BigDecimal salaryPackage;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "Offer status is required")
    private OfferStatus status;

    private String remarks;
}