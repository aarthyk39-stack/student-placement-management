package com.example.studentplacementmanagement.dto.response;

import com.example.studentplacementmanagement.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {

    private Long userId;
    private String username;
    private String email;
    private Role role;
    private String message;
}