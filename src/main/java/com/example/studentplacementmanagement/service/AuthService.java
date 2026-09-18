package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.LoginRequestDTO;
import com.example.studentplacementmanagement.dto.request.RegisterRequestDTO;
import com.example.studentplacementmanagement.dto.response.LoginResponseDTO;
import com.example.studentplacementmanagement.dto.response.RegisterResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO requestDTO);

    LoginResponseDTO refreshAccessToken(String refreshToken);

    void logout(String refreshToken);

    void logoutAllDevices(Long userId);

    RegisterResponseDTO register(RegisterRequestDTO requestDTO);
}