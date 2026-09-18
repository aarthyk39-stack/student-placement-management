package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.entity.RefreshToken;
import com.example.studentplacementmanagement.entity.User;
import com.example.studentplacementmanagement.exception.BusinessValidationException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;

    @Override
    public RefreshToken createRefreshToken(User user) {

        log.info(
                "Creating refresh token for user ID: {}",
                user.getId()
        );

        refreshTokenRepository.revokeAllUserTokens(
                user.getId()
        );

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                UUID.randomUUID().toString()
        );

        refreshToken.setUser(user);

        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusNanos(
                                refreshTokenExpiration * 1_000_000
                        )
        );

        refreshToken.setRevoked(false);

        RefreshToken savedToken =
                refreshTokenRepository.save(
                        refreshToken
                );

        log.info(
                "Refresh token created successfully for user ID: {}",
                user.getId()
        );

        return savedToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(
            String token) {

        log.info("Verifying refresh token");

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Refresh token not found"
                                )
                        );

        if (Boolean.TRUE.equals(
                refreshToken.getRevoked())) {

            throw new BusinessValidationException(
                    "Refresh token has been revoked"
            );
        }

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new BusinessValidationException(
                    "Refresh token has expired"
            );
        }

        if (refreshToken.getUser() == null
                || !Boolean.TRUE.equals(
                refreshToken.getUser().getIsActive())) {

            throw new BusinessValidationException(
                    "User account is inactive"
            );
        }

        return refreshToken;
    }

    @Override
    public void revokeToken(String token) {

        log.info("Revoking refresh token");

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Refresh token not found"
                                )
                        );

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(
                refreshToken
        );

        log.info("Refresh token revoked successfully");
    }

    @Override
    public void revokeAllUserTokens(
            Long userId) {

        log.info(
                "Revoking all refresh tokens for user ID: {}",
                userId
        );

        int revokedCount =
                refreshTokenRepository
                        .revokeAllUserTokens(userId);

        log.info(
                "{} refresh tokens revoked for user ID: {}",
                revokedCount,
                userId
        );
    }

    @Override
    public void deleteExpiredTokens() {

        log.info("Deleting expired refresh tokens");

        int deletedCount =
                refreshTokenRepository
                        .deleteExpiredTokens(
                                LocalDateTime.now()
                        );

        log.info(
                "{} expired refresh tokens deleted",
                deletedCount
        );
    }
}