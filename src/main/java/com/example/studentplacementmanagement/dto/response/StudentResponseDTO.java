package com.example.studentplacementmanagement.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentResponseDTO {

    private Long id;

    private Long userId;

    private String rollNumber;

    private String name;

    private String phone;

    private String department;

    private String degree;

    private Integer graduationYear;

    private BigDecimal cgpa;

    private Integer backlogs;

    private Boolean isPlaced;
}