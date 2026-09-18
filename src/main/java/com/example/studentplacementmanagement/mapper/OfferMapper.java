package com.example.studentplacementmanagement.mapper;

import com.example.studentplacementmanagement.dto.request.OfferRequestDTO;
import com.example.studentplacementmanagement.dto.response.OfferResponseDTO;
import com.example.studentplacementmanagement.entity.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    @Mapping(source = "applicationId", target = "application.id")
    Offer toEntity(OfferRequestDTO dto);

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(target = "studentName", ignore = true)
    @Mapping(target = "companyName", ignore = true)
    @Mapping(target = "jobTitle", ignore = true)
    OfferResponseDTO toResponseDTO(Offer offer);
}