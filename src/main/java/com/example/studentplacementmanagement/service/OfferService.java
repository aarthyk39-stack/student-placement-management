package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.OfferRequestDTO;
import com.example.studentplacementmanagement.dto.response.OfferResponseDTO;
import com.example.studentplacementmanagement.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfferService {

    OfferResponseDTO saveOffer(OfferRequestDTO requestDTO);

    OfferResponseDTO getOfferById(Long id);

    Page<OfferResponseDTO> getAllOffers(Pageable pageable);

    List<OfferResponseDTO> getOffersByStudent(Long studentId);

    List<OfferResponseDTO> getOffersByStatus(OfferStatus status);

    void updateOfferStatus(Long id, OfferStatus status);

    void deleteOffer(Long id);

    long getOfferCountByStatus(OfferStatus status);
}