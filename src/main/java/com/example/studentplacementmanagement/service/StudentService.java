package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.StudentRequestDTO;
import com.example.studentplacementmanagement.dto.response.StudentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface StudentService {

    StudentResponseDTO saveStudent(StudentRequestDTO requestDTO);

    StudentResponseDTO getStudentById(Long id);

    Page<StudentResponseDTO> getAllStudents(Pageable pageable);

    Page<StudentResponseDTO> searchStudents(
            String keyword,
            Pageable pageable
    );

    List<StudentResponseDTO> getStudentsByDepartment(
            String department
    );

    List<StudentResponseDTO> getEligibleStudents(
            BigDecimal minimumCgpa,
            Integer maximumBacklogs
    );

    long getPlacedStudentCount();

    long getUnplacedStudentCount();

    void deleteStudent(Long id);
}