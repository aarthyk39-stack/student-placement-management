package com.example.studentplacementmanagement.mapper;

import com.example.studentplacementmanagement.dto.request.JobDriveRequestDTO;
import com.example.studentplacementmanagement.dto.response.JobDriveResponseDTO;
import com.example.studentplacementmanagement.entity.JobDrive;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobDriveMapper {

    @Mapping(source = "companyId", target = "company.id")
    JobDrive toEntity(JobDriveRequestDTO dto);

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(target = "companyName", ignore = true)
    JobDriveResponseDTO toResponseDTO(JobDrive jobDrive);
}