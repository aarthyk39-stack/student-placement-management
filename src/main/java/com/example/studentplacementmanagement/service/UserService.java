package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.UserRequestDTO;
import com.example.studentplacementmanagement.dto.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponseDTO saveUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById(Long id);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    Page<UserResponseDTO> searchUsers(String keyword, Pageable pageable);

    UserResponseDTO updateUserStatus(Long id, Boolean isActive);

    void deleteUser(Long id);
}