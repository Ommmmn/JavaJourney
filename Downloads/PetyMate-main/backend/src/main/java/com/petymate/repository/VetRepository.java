package com.petymate.repository;

import com.petymate.entity.Vet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VetRepository extends JpaRepository<Vet, Long> {

    @Query("SELECT v FROM Vet v WHERE " +
           "(:city IS NULL OR LOWER(v.city) = LOWER(:city)) AND " +
           "(:state IS NULL OR LOWER(v.state) = LOWER(:state)) AND " +
           "(:specialization IS NULL OR LOWER(v.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "(:verifiedOnly IS NULL OR v.isVerified = :verifiedOnly) AND " +
           "(:minRating IS NULL OR v.rating >= :minRating) AND " +
           "(:maxFee IS NULL OR v.consultationFee <= :maxFee)")
    Page<Vet> findVetsFiltered(
            @Param("city") String city,
            @Param("state") String state,
            @Param("specialization") String specialization,
            @Param("verifiedOnly") Boolean verifiedOnly,
            @Param("minRating") java.math.BigDecimal minRating,
            @Param("maxFee") java.math.BigDecimal maxFee,
            Pageable pageable);

    long countByIsVerifiedFalse();
}
