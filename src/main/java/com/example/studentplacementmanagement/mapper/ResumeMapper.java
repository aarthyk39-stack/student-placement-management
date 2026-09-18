package com.example.studentplacementmanagement.mapper;

import com.example.studentplacementmanagement.dto.response.ResumeResponseDTO;
import com.example.studentplacementmanagement.entity.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(source = "student.id", target = "studentId")
    ResumeResponseDTO toResponseDTO(Resume resume);
}