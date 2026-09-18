package com.example.studentplacementmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyRequestDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 150, message = "Company name must be between 2 and 150 characters")
    private String name;

    @NotBlank(message = "Industry is required")
    private String industry;

    @NotBlank(message = "Location is required")
    private String location;

    @Size(max = 255, message = "Website URL is too long")
    private String website;

    @NotBlank(message = "HR name is required")
    private String hrName;

    @NotBlank(message = "HR email is required")
    @Email(message = "Please provide a valid HR email")
    private String hrEmail;

    @NotBlank(message = "HR phone is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Please provide a valid 10-digit HR phone number"
    )
    private String hrPhone;
}