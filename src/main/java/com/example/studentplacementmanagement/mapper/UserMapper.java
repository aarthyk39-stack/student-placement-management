package com.example.studentplacementmanagement.mapper;

import com.example.studentplacementmanagement.dto.request.UserRequestDTO;
import com.example.studentplacementmanagement.dto.response.UserResponseDTO;
import com.example.studentplacementmanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponseDTO(User user);
}