package com.example.studentplacementmanagement.mapper;

import com.example.studentplacementmanagement.dto.request.StudentRequestDTO;
import com.example.studentplacementmanagement.dto.response.StudentResponseDTO;
import com.example.studentplacementmanagement.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(source = "userId", target = "user.id")
    Student toEntity(StudentRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    StudentResponseDTO toResponseDTO(Student student);
}