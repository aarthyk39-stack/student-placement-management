package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.entity.AuditLog;
import com.example.studentplacementmanagement.entity.User;
import com.example.studentplacementmanagement.repository.AuditLogRepository;
import com.example.studentplacementmanagement.repository.UserRepository;
import com.example.studentplacementmanagement.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuditLog createAuditLog(
            Long userId,
            String action,
            String entityName,
            String entityId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with ID: " + userId
                        )
                );

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .build();

        return auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> getByUserId(
            Long userId,
            Pageable pageable) {

        return auditLogRepository.findByUserId(
                userId,
                pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getByEntityName(
            String entityName) {

        return auditLogRepository.findByEntityName(
                entityName
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getByDateRange(
            LocalDateTime start,
            LocalDateTime end) {

        return auditLogRepository.findByCreatedAtBetween(
                start,
                end
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getEntityHistory(
            String entityName,
            String entityId) {

        return auditLogRepository.findEntityHistory(
                entityName,
                entityId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countByAction(String action) {
        return auditLogRepository.countByAction(action);
    }
}