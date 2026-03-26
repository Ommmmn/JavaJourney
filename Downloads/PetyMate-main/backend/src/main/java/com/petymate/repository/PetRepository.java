package com.petymate.repository;

import com.petymate.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("SELECT p FROM Pet p WHERE p.owner.id = :ownerId")
    List<Pet> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT p FROM Pet p WHERE p.status = 'ACTIVE' AND " +
           "(:species IS NULL OR p.species = :species) AND " +
           "(:breed IS NULL OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :breed, '%'))) AND " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:listingType IS NULL OR p.listingType = :listingType) AND " +
           "(:city IS NULL OR LOWER(p.city) = LOWER(:city)) AND " +
           "(:state IS NULL OR LOWER(p.state) = LOWER(:state)) AND " +
           "(:vaccinationStatus IS NULL OR p.vaccinationStatus = :vaccinationStatus) AND " +
           "(:pedigreeOnly IS NULL OR p.pedigreeCertified = :pedigreeOnly) AND " +
           "(:hasOwnSpace IS NULL OR p.hasOwnSpace = :hasOwnSpace) AND " +
           "(:ageMin IS NULL OR p.ageMonths >= :ageMin) AND " +
           "(:ageMax IS NULL OR p.ageMonths <= :ageMax) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Pet> findPetsFiltered(
            @Param("species") Pet.Species species,
            @Param("breed") String breed,
            @Param("gender") Pet.Gender gender,
            @Param("listingType") Pet.ListingType listingType,
            @Param("city") String city,
            @Param("state") String state,
            @Param("vaccinationStatus") Boolean vaccinationStatus,
            @Param("pedigreeOnly") Boolean pedigreeOnly,
            @Param("hasOwnSpace") Boolean hasOwnSpace,
            @Param("ageMin") Integer ageMin,
            @Param("ageMax") Integer ageMax,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            Pageable pageable);

    long countByStatus(Pet.PetStatus status);

    @Query("SELECT p FROM Pet p WHERE p.status = 'ACTIVE' AND p.listingType IN ('SALE','ADOPTION') AND " +
           "(:species IS NULL OR p.species = :species) AND " +
           "(:city IS NULL OR LOWER(p.city) = LOWER(:city))")
    Page<Pet> findShopPets(@Param("species") Pet.Species species,
                           @Param("city") String city,
                           Pageable pageable);
}
