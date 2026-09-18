package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.User;
import com.example.studentplacementmanagement.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByIsActiveTrue();

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username,
            String email,
            Pageable pageable
    );

    @Query("""
            SELECT u FROM User u
            WHERE u.role = :role
            AND u.isActive = true
            """)
    List<User> findActiveUsersByRole(@Param("role") Role role);

    @Query("""
            SELECT COUNT(u) FROM User u
            WHERE u.role = :role
            AND u.isActive = true
            """)
    long countActiveUsersByRole(@Param("role") Role role);
}