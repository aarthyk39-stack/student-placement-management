package com.example.studentplacementmanagement.dto.response;

import com.example.studentplacementmanagement.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long userId;

    private String username;

    private Role role;
}