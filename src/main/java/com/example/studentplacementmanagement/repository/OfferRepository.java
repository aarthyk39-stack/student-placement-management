package com.example.studentplacementmanagement.repository;

import com.example.studentplacementmanagement.entity.Offer;
import com.example.studentplacementmanagement.enums.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    Optional<Offer> findByApplicationId(Long applicationId);

    Optional<Offer> findByOfferLetterNumber(String offerLetterNumber);

    boolean existsByApplicationId(Long applicationId);

    boolean existsByOfferLetterNumber(String offerLetterNumber);

    List<Offer> findByStatus(OfferStatus status);

    List<Offer> findByApplicationStudentId(Long studentId);

    @Query("""
            SELECT o FROM Offer o
            WHERE o.application.student.id = :studentId
            AND o.status = :status
            """)
    List<Offer> findStudentOffersByStatus(
            @Param("studentId") Long studentId,
            @Param("status") OfferStatus status
    );

    long countByStatus(OfferStatus status);

    @Query("""
            SELECT COUNT(o)
            FROM Offer o
            WHERE o.application.jobDrive.company.id = :companyId
            AND o.status = :status
            """)
    long countCompanyOffersByStatus(
            @Param("companyId") Long companyId,
            @Param("status") OfferStatus status
    );
}