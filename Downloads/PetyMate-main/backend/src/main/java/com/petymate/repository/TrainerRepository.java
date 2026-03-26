package com.petymate.repository;

import com.petymate.entity.Trainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t WHERE " +
           "(:city IS NULL OR LOWER(t.city) = LOWER(:city)) AND " +
           "(:state IS NULL OR LOWER(t.state) = LOWER(:state)) AND " +
           "(:specialization IS NULL OR t.specialization = :specialization) AND " +
           "(:speciesExpertise IS NULL OR t.speciesExpertise LIKE CONCAT('%', :speciesExpertise, '%')) AND " +
           "(:verifiedOnly IS NULL OR t.isVerified = :verifiedOnly) AND " +
           "(:minRating IS NULL OR t.rating >= :minRating) AND " +
           "(:maxFee IS NULL OR t.sessionFeePerHour <= :maxFee)")
    Page<Trainer> findTrainersFiltered(
            @Param("city") String city,
            @Param("state") String state,
            @Param("specialization") Trainer.TrainerSpecialization specialization,
            @Param("speciesExpertise") String speciesExpertise,
            @Param("verifiedOnly") Boolean verifiedOnly,
            @Param("minRating") java.math.BigDecimal minRating,
            @Param("maxFee") java.math.BigDecimal maxFee,
            Pageable pageable);

    long countByIsVerifiedFalse();
}
