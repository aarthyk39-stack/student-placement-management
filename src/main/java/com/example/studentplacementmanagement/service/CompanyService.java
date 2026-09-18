package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.CompanyRequestDTO;
import com.example.studentplacementmanagement.dto.response.CompanyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {

    CompanyResponseDTO saveCompany(
            CompanyRequestDTO requestDTO
    );

    CompanyResponseDTO getCompanyById(
            Long id
    );

    Page<CompanyResponseDTO> getAllCompanies(
            Pageable pageable
    );

    Page<CompanyResponseDTO> searchCompanies(
            String keyword,
            Pageable pageable
    );

    List<CompanyResponseDTO> getCompaniesByIndustry(
            String industry
    );

    List<CompanyResponseDTO> getActiveCompanies();

    long getActiveCompanyCount();

    void deleteCompany(Long id);
}