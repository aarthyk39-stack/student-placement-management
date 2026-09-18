package com.example.studentplacementmanagement.dto.response;

import com.example.studentplacementmanagement.enums.Role;
import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;

    private String username;

    private String email;

    private Role role;

    private Boolean isActive;
}