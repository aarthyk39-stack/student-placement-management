package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository
        extends JpaRepository<Company, Long>,
        JpaSpecificationExecutor<Company> {

    Optional<Company> findByNameIgnoreCase(String name);

    Optional<Company> findByUserId(Long userId);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByUserId(Long userId);

    List<Company> findByIndustryIgnoreCase(String industry);

    List<Company> findByIsActiveTrue();

    Page<Company> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    @Query("""
            SELECT c FROM Company c
            WHERE c.isActive = true
            AND LOWER(c.industry) = LOWER(:industry)
            """)
    List<Company> findActiveCompaniesByIndustry(
            @Param("industry") String industry
    );

    long countByIsActiveTrue();
}