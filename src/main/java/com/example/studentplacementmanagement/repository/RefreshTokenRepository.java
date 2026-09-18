package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserId(Long userId);

    List<RefreshToken> findByExpiryDateBefore(
            LocalDateTime dateTime
    );

    boolean existsByToken(String token);

    @Modifying
    @Query("""
            UPDATE RefreshToken r
            SET r.revoked = true
            WHERE r.user.id = :userId
            """)
    int revokeAllUserTokens(
            @Param("userId") Long userId
    );

    @Modifying
    @Query("""
            DELETE FROM RefreshToken r
            WHERE r.expiryDate < :dateTime
            """)
    int deleteExpiredTokens(
            @Param("dateTime") LocalDateTime dateTime
    );
}