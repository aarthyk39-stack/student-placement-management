package com.example.studentplacementmanagement.mapper;

import com.example.studentplacementmanagement.dto.request.InterviewRequestDTO;
import com.example.studentplacementmanagement.dto.response.InterviewResponseDTO;
import com.example.studentplacementmanagement.entity.Interview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterviewMapper {

    @Mapping(source = "applicationId", target = "application.id")
    Interview toEntity(InterviewRequestDTO dto);

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(target = "studentName", ignore = true)
    @Mapping(target = "jobTitle", ignore = true)
    InterviewResponseDTO toResponseDTO(Interview interview);
}