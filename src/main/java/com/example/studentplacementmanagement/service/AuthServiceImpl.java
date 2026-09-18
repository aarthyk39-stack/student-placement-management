package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.LoginRequestDTO;
import com.example.studentplacementmanagement.dto.request.RegisterRequestDTO;
import com.example.studentplacementmanagement.dto.response.LoginResponseDTO;
import com.example.studentplacementmanagement.dto.response.RegisterResponseDTO;
import com.example.studentplacementmanagement.entity.RefreshToken;
import com.example.studentplacementmanagement.entity.User;
import com.example.studentplacementmanagement.exception.BusinessValidationException;
import com.example.studentplacementmanagement.exception.DuplicateResourceException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.repository.UserRepository;
import com.example.studentplacementmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(
            LoginRequestDTO requestDTO) {

        log.info(
                "Login attempt for: {}",
                requestDTO.getUsernameOrEmail()
        );

        User user = findUser(
                requestDTO.getUsernameOrEmail()
        );

        validateUser(user);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                requestDTO.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String accessToken =
                jwtService.generateAccessToken(user);

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        log.info(
                "User logged in successfully: {}",
                user.getUsername()
        );

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(userDetails.getUsername())
                .role(user.getRole())
                .build();
    }

    @Override
    public LoginResponseDTO refreshAccessToken(
            String refreshToken) {

        log.info("Refreshing access token");

        if (refreshToken == null
                || refreshToken.isBlank()) {

            log.warn("Refresh token is missing");

            throw new BusinessValidationException(
                    "Refresh token is required"
            );
        }

        RefreshToken storedToken =
                refreshTokenService.verifyRefreshToken(
                        refreshToken
                );

        User user =
                storedToken.getUser();

        String accessToken =
                jwtService.generateAccessToken(user);

        log.info(
                "Access token refreshed for user ID: {}",
                user.getId()
        );

        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(storedToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Override
    public void logout(
            String refreshToken) {

        log.info("Logging out user");

        if (refreshToken == null
                || refreshToken.isBlank()) {

            log.warn("Logout failed: refresh token is missing");

            throw new BusinessValidationException(
                    "Refresh token is required"
            );
        }

        refreshTokenService.revokeToken(
                refreshToken
        );

        log.info("User logged out successfully");
    }

    @Override
    public void logoutAllDevices(
            Long userId) {

        log.info(
                "Logging out all devices for user ID: {}",
                userId
        );

        if (!userRepository.existsById(userId)) {

            log.warn(
                    "Logout all devices failed. User not found: {}",
                    userId
            );

            throw new ResourceNotFoundException(
                    "User not found with ID: " + userId
            );
        }

        refreshTokenService.revokeAllUserTokens(
                userId
        );

        log.info(
                "All sessions revoked for user ID: {}",
                userId
        );
    }

    private User findUser(
            String usernameOrEmail) {

        return userRepository
                .findByUsername(usernameOrEmail)
                .orElseGet(() ->
                        userRepository
                                .findByEmail(usernameOrEmail)
                                .orElseThrow(() -> {

                                    log.warn(
                                            "Login failed for username/email: {}",
                                            usernameOrEmail
                                    );

                                    return new BusinessValidationException(
                                            "Invalid username/email or password"
                                    );
                                })
                );
    }

    @Override
    @Transactional
    public RegisterResponseDTO register(
            RegisterRequestDTO requestDTO) {

        log.info(
                "Registration attempt for username: {}",
                requestDTO.getUsername()
        );

        if (userRepository
                .findByUsername(requestDTO.getUsername())
                .isPresent()) {

            log.warn(
                    "Registration failed. Username already exists: {}",
                    requestDTO.getUsername()
            );

            throw new DuplicateResourceException(
                    "Username already exists"
            );
        }

        if (userRepository
                .findByEmail(requestDTO.getEmail())
                .isPresent()) {

            log.warn(
                    "Registration failed. Email already exists: {}",
                    requestDTO.getEmail()
            );

            throw new DuplicateResourceException(
                    "Email already exists"
            );
        }

        User user = new User();

        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        requestDTO.getPassword()
                )
        );

        user.setRole(requestDTO.getRole());
        user.setIsActive(true);

        User savedUser =
                userRepository.save(user);

        log.info(
                "User registered successfully. User ID: {}, Username: {}",
                savedUser.getId(),
                savedUser.getUsername()
        );

        return RegisterResponseDTO.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .message("User registered successfully")
                .build();
    }

    private void validateUser(User user) {

        if (!Boolean.TRUE.equals(
                user.getIsActive())) {

            log.warn(
                    "Inactive user attempted login: {}",
                    user.getUsername()
            );

            throw new BusinessValidationException(
                    "User account is inactive"
            );
        }
    }
}