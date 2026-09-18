package com.example.studentplacementmanagement.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentRequestDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Roll number is required")
    @Size(max = 30, message = "Roll number must not exceed 30 characters")
    private String rollNumber;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100,
            message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Please provide a valid 10-digit phone number"
    )
    private String phone;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotNull(message = "Graduation year is required")
    @Min(value = 2000, message = "Invalid graduation year")
    private Integer graduationYear;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0",
            message = "CGPA cannot be less than 0")
    @DecimalMax(value = "10.0",
            message = "CGPA cannot be greater than 10")
    private BigDecimal cgpa;

    @NotNull(message = "Backlogs are required")
    @Min(value = 0,
            message = "Backlogs cannot be negative")
    private Integer backlogs;
}