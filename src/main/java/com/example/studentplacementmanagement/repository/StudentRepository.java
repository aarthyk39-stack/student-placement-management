package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository
        extends JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student> {

    Optional<Student> findByRollNumber(String rollNumber);

    Optional<Student> findByUserId(Long userId);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByUserId(Long userId);

    List<Student> findByDepartmentIgnoreCase(String department);

    List<Student> findByDegreeIgnoreCase(String degree);

    List<Student> findByIsPlacedFalse();

    List<Student> findByCgpaGreaterThanEqual(BigDecimal cgpa);

    List<Student> findByBacklogsLessThanEqual(Integer backlogs);

    Page<Student> findByNameContainingIgnoreCaseOrRollNumberContainingIgnoreCase(
            String name,
            String rollNumber,
            Pageable pageable
    );

    @Query("""
            SELECT s FROM Student s
            WHERE s.cgpa >= :minimumCgpa
            AND s.backlogs <= :maximumBacklogs
            AND s.isPlaced = false
            """)
    List<Student> findEligibleStudents(
            @Param("minimumCgpa") BigDecimal minimumCgpa,
            @Param("maximumBacklogs") Integer maximumBacklogs
    );

    @Query("""
            SELECT s FROM Student s
            WHERE LOWER(s.department) = LOWER(:department)
            AND s.cgpa >= :minimumCgpa
            AND s.isPlaced = false
            """)
    List<Student> findEligibleStudentsByDepartment(
            @Param("department") String department,
            @Param("minimumCgpa") BigDecimal minimumCgpa
    );

    @Query("""
            SELECT AVG(s.cgpa)
            FROM Student s
            WHERE s.department = :department
            """)
    BigDecimal findAverageCgpaByDepartment(
            @Param("department") String department
    );

    long countByIsPlacedTrue();

    long countByIsPlacedFalse();
}