package com.example.studentplacementmanagement.service.impl;

import com.example.studentplacementmanagement.dto.request.UserRequestDTO;
import com.example.studentplacementmanagement.dto.response.UserResponseDTO;
import com.example.studentplacementmanagement.entity.User;
import com.example.studentplacementmanagement.exception.DuplicateResourceException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.mapper.UserMapper;
import com.example.studentplacementmanagement.repository.UserRepository;
import com.example.studentplacementmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO saveUser(UserRequestDTO requestDTO) {

        User user;

        // =========================
        // CREATE
        // =========================
        if (requestDTO.getId() == null) {

            if (userRepository.existsByUsername(requestDTO.getUsername())) {
                throw new DuplicateResourceException(
                        "Username already exists: " + requestDTO.getUsername()
                );
            }

            if (userRepository.existsByEmail(requestDTO.getEmail())) {
                throw new DuplicateResourceException(
                        "Email already exists: " + requestDTO.getEmail()
                );
            }

            user = userMapper.toEntity(requestDTO);

            user.setPassword(
                    passwordEncoder.encode(requestDTO.getPassword())
            );

            user.setIsActive(true);

            log.info(
                    "Creating new user with username: {}",
                    requestDTO.getUsername()
            );

        }

        // =========================
        // UPDATE
        // =========================
        else {

            user = userRepository.findById(requestDTO.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with ID: "
                                            + requestDTO.getId()
                            )
                    );

            // Check username belongs to another user
            userRepository.findByUsername(requestDTO.getUsername())
                    .ifPresent(existingUser -> {

                        if (!existingUser.getId()
                                .equals(requestDTO.getId())) {

                            throw new DuplicateResourceException(
                                    "Username already exists: "
                                            + requestDTO.getUsername()
                            );
                        }
                    });

            // Check email belongs to another user
            userRepository.findByEmail(requestDTO.getEmail())
                    .ifPresent(existingUser -> {

                        if (!existingUser.getId()
                                .equals(requestDTO.getId())) {

                            throw new DuplicateResourceException(
                                    "Email already exists: "
                                            + requestDTO.getEmail()
                            );
                        }
                    });

            user.setUsername(requestDTO.getUsername());
            user.setEmail(requestDTO.getEmail());
            user.setRole(requestDTO.getRole());

            // Password update only if provided
            if (requestDTO.getPassword() != null
                    && !requestDTO.getPassword().isBlank()) {

                user.setPassword(
                        passwordEncoder.encode(
                                requestDTO.getPassword()
                        )
                );
            }

            log.info(
                    "Updating user with ID: {}",
                    requestDTO.getId()
            );
        }

        User savedUser = userRepository.save(user);

        log.info(
                "User saved successfully with ID: {}",
                savedUser.getId()
        );

        return userMapper.toResponseDTO(savedUser);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {

        log.info("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );

        return userMapper.toResponseDTO(user);
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(
            Pageable pageable) {

        log.info("Fetching all users");

        return userRepository
                .findAll(pageable)
                .map(userMapper::toResponseDTO);
    }

    // SEARCH
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(
            String keyword,
            Pageable pageable) {

        log.info(
                "Searching users with keyword: {}",
                keyword
        );

        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword,
                        keyword,
                        pageable
                )
                .map(userMapper::toResponseDTO);
    }

    // ACTIVATE / DEACTIVATE
    @Override
    public UserResponseDTO updateUserStatus(
            Long id,
            Boolean isActive) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );

        user.setIsActive(isActive);

        log.info(
                "User ID {} status changed to {}",
                id,
                isActive
        );

        return userMapper.toResponseDTO(user);
    }

    // SOFT DELETE
    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );

        user.setIsActive(false);

        log.info(
                "User ID {} deactivated successfully",
                id
        );
    }
}