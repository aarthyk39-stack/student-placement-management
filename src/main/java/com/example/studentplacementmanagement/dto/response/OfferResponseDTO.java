package com.example.studentplacementmanagement.dto.response;

import com.example.studentplacementmanagement.enums.OfferStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OfferResponseDTO {

    private Long id;

    private Long applicationId;

    private String studentName;

    private String companyName;

    private String jobTitle;

    private String offerLetterNumber;

    private BigDecimal salaryPackage;

    private LocalDate joiningDate;

    private OfferStatus status;

    private LocalDateTime issuedAt;

    private String remarks;
}