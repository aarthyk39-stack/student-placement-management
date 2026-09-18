package com.example.studentplacementmanagement.dto.response;

import lombok.Data;

@Data
public class CompanyResponseDTO {

    private Long id;

    private Long userId;

    private String name;

    private String industry;

    private String location;

    private String website;

    private String hrName;

    private String hrEmail;

    private String hrPhone;

    private Boolean isActive;
}