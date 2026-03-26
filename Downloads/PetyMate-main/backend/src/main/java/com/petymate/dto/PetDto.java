package com.petymate.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PetDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreatePetRequest {
        @NotBlank(message = "Pet name is required")
        private String name;
        @NotNull(message = "Species is required")
        private String species;
        private String breed;
        private Integer ageMonths;
        @NotNull(message = "Gender is required")
        private String gender;
        private String color;
        private BigDecimal weightKg;
        private Boolean vaccinationStatus;
        private Boolean neutered;
        private Boolean pedigreeCertified;
        private Boolean hasOwnSpace;
        private String healthStatus;
        private String bio;
        private String city;
        private String state;
        private String pincode;
        @NotNull(message = "Listing type is required")
        private String listingType;
        private BigDecimal price;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PetResponse {
        private Long id;
        private Long ownerId;
        private String ownerName;
        private String name;
        private String species;
        private String breed;
        private Integer ageMonths;
        private String gender;
        private String color;
        private BigDecimal weightKg;
        private Boolean vaccinationStatus;
        private Boolean neutered;
        private Boolean pedigreeCertified;
        private Boolean hasOwnSpace;
        private String healthStatus;
        private String bio;
        private String city;
        private String state;
        private String pincode;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String listingType;
        private BigDecimal price;
        private String status;
        private List<PetPhotoDto> photos;
        private Double distanceKm;
        private String ownerPhone;
        private String ownerEmail;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PetPhotoDto {
        private Long id;
        private String photoUrl;
        private Boolean isPrimary;
        private Integer orderIndex;
    }
}
