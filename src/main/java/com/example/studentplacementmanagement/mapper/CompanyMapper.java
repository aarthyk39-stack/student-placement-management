package com.example.studentplacementmanagement.mapper;

import com.example.studentplacementmanagement.dto.request.CompanyRequestDTO;
import com.example.studentplacementmanagement.dto.response.CompanyResponseDTO;
import com.example.studentplacementmanagement.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(source = "userId", target = "user.id")
    Company toEntity(CompanyRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    CompanyResponseDTO toResponseDTO(Company company);
}