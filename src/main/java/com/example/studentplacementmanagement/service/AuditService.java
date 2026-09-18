package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditService {

    AuditLog createAuditLog(
            Long userId,
            String action,
            String entityName,
            String entityId
    );

    List<AuditLog> getByUserId(Long userId);

    Page<AuditLog> getByUserId(
            Long userId,
            Pageable pageable
    );

    List<AuditLog> getByAction(String action);

    List<AuditLog> getByEntityName(String entityName);

    List<AuditLog> getByDateRange(
            LocalDateTime start,
            LocalDateTime end
    );

    List<AuditLog> getEntityHistory(
            String entityName,
            String entityId
    );

    long countByAction(String action);
}