package com.example.studentplacementmanagement.mapper;

import com.example.studentplacementmanagement.dto.request.ApplicationRequestDTO;
import com.example.studentplacementmanagement.dto.response.ApplicationResponseDTO;
import com.example.studentplacementmanagement.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(source = "studentId", target = "student.id")
    @Mapping(source = "jobDriveId", target = "jobDrive.id")
    Application toEntity(ApplicationRequestDTO dto);

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "jobDrive.id", target = "jobDriveId")
    @Mapping(target = "studentName", ignore = true)
    @Mapping(target = "jobTitle", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    ApplicationResponseDTO toResponseDTO(Application application);
}