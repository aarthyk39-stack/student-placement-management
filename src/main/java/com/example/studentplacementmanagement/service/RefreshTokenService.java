package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.entity.RefreshToken;
import com.example.studentplacementmanagement.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeToken(String token);

    void revokeAllUserTokens(Long userId);

    void deleteExpiredTokens();
}