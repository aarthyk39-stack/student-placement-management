package com.example.studentplacementmanagement.controller;

import com.example.studentplacementmanagement.dto.request.LoginRequestDTO;
import com.example.studentplacementmanagement.dto.request.RegisterRequestDTO;
import com.example.studentplacementmanagement.dto.response.LoginResponseDTO;
import com.example.studentplacementmanagement.dto.response.RegisterResponseDTO;
import com.example.studentplacementmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "APIs for user registration and authentication"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username or email already exists"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO requestDTO) {

        RegisterResponseDTO response =
                authService.register(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a user and generates access and refresh tokens"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid login request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid username/email or password"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO requestDTO) {

        LoginResponseDTO response =
                authService.login(requestDTO);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token refreshed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token is missing or invalid"
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshAccessToken(
            @Parameter(
                    description = "Valid refresh token"
            )
            @RequestParam String refreshToken) {

        LoginResponseDTO response =
                authService.refreshAccessToken(
                        refreshToken
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Logout",
            description = "Revokes the specified refresh token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Logout successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token is missing or invalid"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(
                    description = "Refresh token to revoke"
            )
            @RequestParam String refreshToken) {

        authService.logout(refreshToken);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Logout from all devices",
            description = "Revokes all refresh tokens associated with a user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "All sessions revoked successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PostMapping("/logout-all/{userId}")
    public ResponseEntity<Void> logoutAllDevices(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @PathVariable Long userId) {

        authService.logoutAllDevices(userId);

        return ResponseEntity.noContent().build();
    }
}