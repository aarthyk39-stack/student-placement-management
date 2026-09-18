package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.CompanyRequestDTO;
import com.example.studentplacementmanagement.dto.response.CompanyResponseDTO;
import com.example.studentplacementmanagement.entity.Company;
import com.example.studentplacementmanagement.entity.User;
import com.example.studentplacementmanagement.enums.Role;
import com.example.studentplacementmanagement.exception.DuplicateResourceException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.mapper.CompanyMapper;
import com.example.studentplacementmanagement.repository.CompanyRepository;
import com.example.studentplacementmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    private final CompanyMapper companyMapper;


    // =====================================================
    // CREATE + UPDATE COMPANY
    // =====================================================

    @Override
    public CompanyResponseDTO saveCompany(
            CompanyRequestDTO requestDTO) {

        Company company;

        // =================================================
        // CREATE
        // =================================================

        if (requestDTO.getId() == null) {

            log.info(
                    "Creating company with name: {}",
                    requestDTO.getName()
            );

            // Check duplicate company name
            if (companyRepository.existsByNameIgnoreCase(
                    requestDTO.getName())) {

                throw new DuplicateResourceException(
                        "Company already exists with name: "
                                + requestDTO.getName()
                );
            }

            // Check whether user already has company profile
            if (companyRepository.existsByUserId(
                    requestDTO.getUserId())) {

                throw new DuplicateResourceException(
                        "Company profile already exists for user ID: "
                                + requestDTO.getUserId()
                );
            }

            // Get and validate User
            User user = getCompanyUser(
                    requestDTO.getUserId()
            );

            // DTO → Entity
            company = companyMapper.toEntity(
                    requestDTO
            );

            // Set actual User entity
            company.setUser(user);

            // New company is active
            company.setIsActive(true);
        }

        // =================================================
        // UPDATE
        // =================================================

        else {

            log.info(
                    "Updating company with ID: {}",
                    requestDTO.getId()
            );

            // Find existing company
            company = companyRepository.findById(
                    requestDTO.getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Company not found with ID: "
                                    + requestDTO.getId()
                    )
            );

            // Check duplicate company name
            companyRepository.findByNameIgnoreCase(
                    requestDTO.getName()
            ).ifPresent(existingCompany -> {

                if (!existingCompany.getId()
                        .equals(requestDTO.getId())) {

                    throw new DuplicateResourceException(
                            "Company already exists with name: "
                                    + requestDTO.getName()
                    );
                }
            });

            // Check User → Company relationship
            companyRepository.findByUserId(
                    requestDTO.getUserId()
            ).ifPresent(existingCompany -> {

                if (!existingCompany.getId()
                        .equals(requestDTO.getId())) {

                    throw new DuplicateResourceException(
                            "User is already linked to another company"
                    );
                }
            });

            // Get actual User
            User user = getCompanyUser(
                    requestDTO.getUserId()
            );

            // Update relationship
            company.setUser(user);

            // Update fields
            company.setName(
                    requestDTO.getName()
            );

            company.setIndustry(
                    requestDTO.getIndustry()
            );

            company.setLocation(
                    requestDTO.getLocation()
            );

            company.setWebsite(
                    requestDTO.getWebsite()
            );

            company.setHrName(
                    requestDTO.getHrName()
            );

            company.setHrEmail(
                    requestDTO.getHrEmail()
            );

            company.setHrPhone(
                    requestDTO.getHrPhone()
            );

            // Don't change isActive during normal update.
        }

        // =================================================
        // SAVE
        // =================================================

        Company savedCompany =
                companyRepository.save(company);

        log.info(
                "Company saved successfully with ID: {}",
                savedCompany.getId()
        );

        return companyMapper.toResponseDTO(
                savedCompany
        );
    }


    // =====================================================
    // GET COMPANY BY ID
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyById(Long id) {

        log.info(
                "Fetching company with ID: {}",
                id
        );

        Company company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Company not found with ID: " + id
                        )
                );

        return companyMapper.toResponseDTO(company);
    }


    // =====================================================
    // GET ALL COMPANIES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponseDTO> getAllCompanies(
            Pageable pageable) {

        log.info("Fetching all companies");

        return companyRepository
                .findAll(pageable)
                .map(companyMapper::toResponseDTO);
    }


    // =====================================================
    // SEARCH COMPANIES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyResponseDTO> searchCompanies(
            String keyword,
            Pageable pageable) {

        log.info(
                "Searching companies with keyword: {}",
                keyword
        );

        return companyRepository
                .findByNameContainingIgnoreCase(
                        keyword,
                        pageable
                )
                .map(companyMapper::toResponseDTO);
    }


    // =====================================================
    // GET COMPANIES BY INDUSTRY
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getCompaniesByIndustry(
            String industry) {

        log.info(
                "Fetching companies from industry: {}",
                industry
        );

        return companyRepository
                .findByIndustryIgnoreCase(industry)
                .stream()
                .map(companyMapper::toResponseDTO)
                .toList();
    }


    // =====================================================
    // GET ACTIVE COMPANIES
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getActiveCompanies() {

        log.info("Fetching active companies");

        return companyRepository
                .findByIsActiveTrue()
                .stream()
                .map(companyMapper::toResponseDTO)
                .toList();
    }


    // =====================================================
    // ACTIVE COMPANY COUNT
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public long getActiveCompanyCount() {

        return companyRepository
                .countByIsActiveTrue();
    }


    // =====================================================
    // SOFT DELETE COMPANY
    // =====================================================

    @Override
    public void deleteCompany(Long id) {

        log.info(
                "Soft deleting company with ID: {}",
                id
        );

        Company company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Company not found with ID: " + id
                        )
                );

        // Soft delete
        company.setIsActive(false);

        companyRepository.save(company);

        log.info(
                "Company deactivated successfully with ID: {}",
                id
        );
    }


    // =====================================================
    // HELPER METHOD
    // =====================================================

    private User getCompanyUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + userId
                        )
                );

        // User must have COMPANY role
        if (user.getRole() != Role.COMPANY) {

            throw new IllegalArgumentException(
                    "User must have COMPANY role"
            );
        }

        // User must be active
        if (!Boolean.TRUE.equals(
                user.getIsActive())) {

            throw new IllegalArgumentException(
                    "User account is inactive"
            );
        }

        return user;
    }
}