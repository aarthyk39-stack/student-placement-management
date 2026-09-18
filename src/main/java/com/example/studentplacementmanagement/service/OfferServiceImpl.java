package com.example.studentplacementmanagement.service;

import com.example.studentplacementmanagement.dto.request.OfferRequestDTO;
import com.example.studentplacementmanagement.dto.response.OfferResponseDTO;
import com.example.studentplacementmanagement.entity.Application;
import com.example.studentplacementmanagement.entity.Offer;
import com.example.studentplacementmanagement.entity.Student;
import com.example.studentplacementmanagement.enums.ApplicationStatus;
import com.example.studentplacementmanagement.enums.OfferStatus;
import com.example.studentplacementmanagement.exception.BusinessValidationException;
import com.example.studentplacementmanagement.exception.DuplicateResourceException;
import com.example.studentplacementmanagement.exception.ResourceNotFoundException;
import com.example.studentplacementmanagement.mapper.OfferMapper;
import com.example.studentplacementmanagement.repository.ApplicationRepository;
import com.example.studentplacementmanagement.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;

    private final ApplicationRepository applicationRepository;

    private final OfferMapper offerMapper;

    @Override
    public OfferResponseDTO saveOffer(
            OfferRequestDTO requestDTO) {

        Offer offer;

        if (requestDTO.getId() == null) {

            log.info(
                    "Creating offer for application ID: {}",
                    requestDTO.getApplicationId()
            );

            Application application =
                    getApplication(
                            requestDTO.getApplicationId()
                    );

            validateApplicationForOffer(application);

            if (offerRepository.existsByApplicationId(
                    application.getId())) {

                throw new DuplicateResourceException(
                        "Offer already exists for application ID: "
                                + application.getId()
                );
            }

            if (offerRepository.existsByOfferLetterNumber(
                    requestDTO.getOfferLetterNumber())) {

                throw new DuplicateResourceException(
                        "Offer letter number already exists: "
                                + requestDTO.getOfferLetterNumber()
                );
            }

            validateJoiningDate(
                    requestDTO.getJoiningDate()
            );

            offer = offerMapper.toEntity(requestDTO);

            offer.setApplication(application);

            offer.setIssuedAt(
                    LocalDateTime.now()
            );

            if (offer.getStatus() == null) {
                offer.setStatus(
                        OfferStatus.ISSUED
                );
            }

            application.setStatus(
                    ApplicationStatus.OFFERED
            );

            applicationRepository.save(application);

        } else {

            log.info(
                    "Updating offer with ID: {}",
                    requestDTO.getId()
            );

            offer = offerRepository.findById(
                    requestDTO.getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Offer not found with ID: "
                                    + requestDTO.getId()
                    )
            );

            if (requestDTO.getApplicationId() == null) {
                throw new BusinessValidationException(
                        "Application ID is required"
                );
            }

            if (!offer.getApplication()
                    .getId()
                    .equals(requestDTO.getApplicationId())) {

                throw new BusinessValidationException(
                        "Application cannot be changed for an existing offer"
                );
            }

            if (!offer.getOfferLetterNumber()
                    .equals(requestDTO.getOfferLetterNumber())) {

                if (offerRepository.existsByOfferLetterNumber(
                        requestDTO.getOfferLetterNumber())) {

                    throw new DuplicateResourceException(
                            "Offer letter number already exists: "
                                    + requestDTO.getOfferLetterNumber()
                    );
                }

                offer.setOfferLetterNumber(
                        requestDTO.getOfferLetterNumber()
                );
            }

            validateJoiningDate(
                    requestDTO.getJoiningDate()
            );

            offer.setSalaryPackage(
                    requestDTO.getSalaryPackage()
            );

            offer.setJoiningDate(
                    requestDTO.getJoiningDate()
            );

            offer.setRemarks(
                    requestDTO.getRemarks()
            );
        }

        Offer savedOffer =
                offerRepository.save(offer);

        log.info(
                "Offer saved successfully with ID: {}",
                savedOffer.getId()
        );

        return buildResponse(savedOffer);
    }

    @Override
    @Transactional(readOnly = true)
    public OfferResponseDTO getOfferById(Long id) {

        log.info(
                "Fetching offer with ID: {}",
                id
        );

        Offer offer = offerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Offer not found with ID: " + id
                        )
                );

        return buildResponse(offer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OfferResponseDTO> getAllOffers(
            Pageable pageable) {

        log.info("Fetching all offers");

        return offerRepository
                .findAll(pageable)
                .map(this::buildResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferResponseDTO> getOffersByStudent(
            Long studentId) {

        log.info(
                "Fetching offers for student ID: {}",
                studentId
        );

        return offerRepository
                .findByApplicationStudentId(studentId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferResponseDTO> getOffersByStatus(
            OfferStatus status) {

        log.info(
                "Fetching offers with status: {}",
                status
        );

        return offerRepository
                .findByStatus(status)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public void updateOfferStatus(
            Long id,
            OfferStatus newStatus) {

        log.info(
                "Updating offer {} status to {}",
                id,
                newStatus
        );

        if (newStatus == null) {
            throw new BusinessValidationException(
                    "Offer status is required"
            );
        }

        Offer offer = offerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Offer not found with ID: " + id
                        )
                );

        OfferStatus currentStatus =
                offer.getStatus();

        validateStatusTransition(
                currentStatus,
                newStatus
        );

        offer.setStatus(newStatus);

        Application application =
                offer.getApplication();

        if (newStatus == OfferStatus.ACCEPTED) {

            application.setStatus(
                    ApplicationStatus.PLACED
            );

            Student student =
                    application.getStudent();

            if (student != null) {
                student.setIsPlaced(true);
            }

        } else if (newStatus == OfferStatus.REJECTED) {

            application.setStatus(
                    ApplicationStatus.SELECTED
            );
        }

        offerRepository.save(offer);

        applicationRepository.save(application);

        log.info(
                "Offer status updated successfully"
        );
    }

    @Override
    public void deleteOffer(Long id) {

        log.info(
                "Deleting offer with ID: {}",
                id
        );

        Offer offer = offerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Offer not found with ID: " + id
                        )
                );

        if (offer.getStatus() ==
                OfferStatus.ACCEPTED) {

            throw new BusinessValidationException(
                    "Accepted offer cannot be deleted"
            );
        }

        offer.setStatus(
                OfferStatus.EXPIRED
        );

        offerRepository.save(offer);

        log.info(
                "Offer expired successfully"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long getOfferCountByStatus(
            OfferStatus status) {

        return offerRepository
                .countByStatus(status);
    }

    private Application getApplication(
            Long applicationId) {

        return applicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with ID: "
                                        + applicationId
                        )
                );
    }

    private void validateApplicationForOffer(
            Application application) {

        if (application.getStatus() !=
                ApplicationStatus.SELECTED) {

            throw new BusinessValidationException(
                    "Offer can be created only for a selected application"
            );
        }

        if (Boolean.TRUE.equals(
                application.getStudent()
                        .getIsPlaced())) {

            throw new BusinessValidationException(
                    "Offer cannot be created for an already placed student"
            );
        }
    }

    private void validateJoiningDate(
            LocalDate joiningDate) {

        if (joiningDate == null) {

            throw new BusinessValidationException(
                    "Joining date is required"
            );
        }

        if (joiningDate.isBefore(
                LocalDate.now())) {

            throw new BusinessValidationException(
                    "Joining date cannot be in the past"
            );
        }
    }

    private void validateStatusTransition(
            OfferStatus currentStatus,
            OfferStatus newStatus) {

        if (currentStatus == null) {
            return;
        }

        if (currentStatus == OfferStatus.ACCEPTED) {

            throw new BusinessValidationException(
                    "Accepted offer cannot be changed"
            );
        }

        if (currentStatus == OfferStatus.REJECTED) {

            throw new BusinessValidationException(
                    "Rejected offer cannot be changed"
            );
        }

        if (currentStatus == OfferStatus.EXPIRED) {

            throw new BusinessValidationException(
                    "Expired offer cannot be changed"
            );
        }

        if (currentStatus == OfferStatus.ISSUED
                && newStatus != OfferStatus.ACCEPTED
                && newStatus != OfferStatus.REJECTED
                && newStatus != OfferStatus.EXPIRED) {

            throw new BusinessValidationException(
                    "Invalid offer status transition"
            );
        }
    }

    private OfferResponseDTO buildResponse(
            Offer offer) {

        OfferResponseDTO response =
                offerMapper.toResponseDTO(offer);

        Application application =
                offer.getApplication();

        if (application != null) {

            response.setApplicationId(
                    application.getId()
            );

            if (application.getStudent() != null) {

                response.setStudentName(
                        application.getStudent()
                                .getName()
                );
            }

            if (application.getJobDrive() != null
                    && application.getJobDrive()
                    .getCompany() != null) {

                response.setCompanyName(
                        application.getJobDrive()
                                .getCompany()
                                .getName()
                );
            }

            if (application.getJobDrive() != null) {

                response.setJobTitle(
                        application.getJobDrive()
                                .getJobTitle()
                );
            }
        }

        return response;
    }
}