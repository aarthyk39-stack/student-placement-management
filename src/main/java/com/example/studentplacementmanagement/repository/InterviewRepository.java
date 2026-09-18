package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.Interview;
import com.example.studentplacementmanagement.enums.InterviewResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository
        extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationId(Long applicationId);

    List<Interview> findByResult(InterviewResult result);

    List<Interview> findByScheduledAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Interview> findByScheduledAtAfter(
            LocalDateTime dateTime
    );

    Page<Interview> findByApplicationStudentId(
            Long studentId,
            Pageable pageable
    );

    @Query("""
            SELECT i FROM Interview i
            WHERE i.scheduledAt BETWEEN :start AND :end
            ORDER BY i.scheduledAt ASC
            """)
    List<Interview> findUpcomingInterviews(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            SELECT i FROM Interview i
            WHERE i.result = :result
            AND i.scheduledAt < :currentTime
            """)
    List<Interview> findPendingPastInterviews(
            @Param("result") InterviewResult result,
            @Param("currentTime") LocalDateTime currentTime
    );

    long countByApplicationJobDriveId(Long jobDriveId);
}