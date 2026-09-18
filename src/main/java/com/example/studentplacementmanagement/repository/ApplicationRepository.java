package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.Application;
import com.example.studentplacementmanagement.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    List<Application> findByStudentId(Long studentId);

    List<Application> findByJobDriveId(Long jobDriveId);

    List<Application> findByStatus(ApplicationStatus status);

    List<Application> findByStudentIdAndStatus(
            Long studentId,
            ApplicationStatus status
    );

    List<Application> findByJobDriveIdAndStatus(
            Long jobDriveId,
            ApplicationStatus status
    );

    Optional<Application> findByStudentIdAndJobDriveId(
            Long studentId,
            Long jobDriveId
    );

    boolean existsByStudentIdAndJobDriveId(
            Long studentId,
            Long jobDriveId
    );

    Page<Application> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<Application> findByJobDriveId(
            Long jobDriveId,
            Pageable pageable
    );

    @Query("""
            SELECT a FROM Application a
            WHERE a.jobDrive.id = :jobDriveId
            AND a.status = :status
            """)
    List<Application> findApplicationsByDriveAndStatus(
            @Param("jobDriveId") Long jobDriveId,
            @Param("status") ApplicationStatus status
    );

    @Query("""
            SELECT a FROM Application a
            WHERE a.student.id = :studentId
            AND a.status IN (
                com.example.studentplacementmanagement.enums.ApplicationStatus.SELECTED,
                com.example.studentplacementmanagement.enums.ApplicationStatus.OFFERED,
                com.example.studentplacementmanagement.enums.ApplicationStatus.PLACED
            )
            """)
    List<Application> findStudentPlacementHistory(
            @Param("studentId") Long studentId
    );

    long countByJobDriveId(Long jobDriveId);

    long countByJobDriveIdAndStatus(
            Long jobDriveId,
            ApplicationStatus status
    );

    long countByStatus(ApplicationStatus status);

    @Query("""
            SELECT a.status, COUNT(a)
            FROM Application a
            WHERE a.jobDrive.id = :jobDriveId
            GROUP BY a.status
            """)
    List<Object[]> getApplicationStatistics(
            @Param("jobDriveId") Long jobDriveId
    );
}