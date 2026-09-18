package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByEntityName(String entityName);

    Page<AuditLog> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<AuditLog> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
            SELECT a FROM AuditLog a
            WHERE a.entityName = :entityName
            AND a.entityId = :entityId
            ORDER BY a.createdAt DESC
            """)
    List<AuditLog> findEntityHistory(
            @Param("entityName") String entityName,
            @Param("entityId") String entityId
    );

    long countByAction(String action);
}