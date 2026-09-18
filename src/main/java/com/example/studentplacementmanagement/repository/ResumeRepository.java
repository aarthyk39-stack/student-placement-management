package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    Optional<Resume> findByStudentId(Long studentId);

    boolean existsByStudentId(Long studentId);

    void deleteByStudentId(Long studentId);
}