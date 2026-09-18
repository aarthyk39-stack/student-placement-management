package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.JobDrive;
import com.example.studentplacementmanagement.enums.DriveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobDriveRepository
        extends JpaRepository<JobDrive, Long>,
        JpaSpecificationExecutor<JobDrive> {

    List<JobDrive> findByCompanyId(Long companyId);

    List<JobDrive> findByStatus(DriveStatus status);

    List<JobDrive> findByCompanyIdAndStatus(
            Long companyId,
            DriveStatus status
    );

    Page<JobDrive> findByJobTitleContainingIgnoreCase(
            String jobTitle,
            Pageable pageable
    );

    List<JobDrive> findByApplicationDeadlineBefore(
            LocalDateTime dateTime
    );

    List<JobDrive> findByApplicationDeadlineAfter(
            LocalDateTime dateTime
    );

    @Query("""
            SELECT j FROM JobDrive j
            WHERE j.status = :status
            AND j.minimumCgpa <= :cgpa
            AND j.maximumBacklogs >= :backlogs
            AND (
                j.eligibleDepartment IS NULL
                OR LOWER(j.eligibleDepartment) = LOWER(:department)
            )
            """)
    List<JobDrive> findEligibleJobDrives(
            @Param("cgpa") BigDecimal cgpa,
            @Param("backlogs") Integer backlogs,
            @Param("department") String department,
            @Param("status") DriveStatus status
    );

    @Query("""
            SELECT j FROM JobDrive j
            WHERE j.status = :status
            AND j.applicationDeadline < :currentTime
            """)
    List<JobDrive> findExpiredDrives(
            @Param("status") DriveStatus status,
            @Param("currentTime") LocalDateTime currentTime
    );

    @Query("""
            SELECT j FROM JobDrive j
            WHERE j.status = :status
            AND j.applicationDeadline > :currentTime
            ORDER BY j.applicationDeadline ASC
            """)
    List<JobDrive> findOpenDrives(
            @Param("status") DriveStatus status,
            @Param("currentTime") LocalDateTime currentTime
    );

    @Query("""
            SELECT j FROM JobDrive j
            WHERE j.salaryPackage >= :minimumSalary
            AND j.status = :status
            """)
    List<JobDrive> findByMinimumSalary(
            @Param("minimumSalary") BigDecimal minimumSalary,
            @Param("status") DriveStatus status
    );

    long countByCompanyId(Long companyId);

    long countByStatus(DriveStatus status);
}