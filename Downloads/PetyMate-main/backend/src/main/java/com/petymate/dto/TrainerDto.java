package com.petymate.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TrainerDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TrainerResponse {
        private Long id;
        private String name;
        private String specialization;
        private String speciesExpertise;
        private Integer experienceYears;
        private String certification;
        private String bio;
        private String city;
        private String state;
        private String phone;
        private String email;
        private String photoUrl;
        private BigDecimal sessionFeePerHour;
        private String sessionModes;
        private String availableDays;
        private String availableHours;
        private BigDecimal rating;
        private Integer reviewCount;
        private Integer totalSessions;
        private Boolean isVerified;
        private List<TrainingPackageDto> packages;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TrainingPackageDto {
        private Long id;
        private String name;
        private String description;
        private Integer sessionsCount;
        private BigDecimal price;
        private Integer validityDays;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateSessionOrderRequest {
        @NotNull
        private Long trainerId;
        @NotNull
        private Long petId;
        @NotNull
        private LocalDate date;
        @NotNull
        private LocalTime time;
        @NotNull @Min(1) @Max(3)
        private Integer durationHours;
        @NotBlank
        private String mode;
        private String focusArea;
        private String petCurrentIssues;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SessionOrderResponse {
        private String razorpayOrderId;
        private int amount;
        private String currency;
        private String keyId;
        private BigDecimal totalFee;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VerifySessionRequest {
        @NotBlank
        private String razorpayOrderId;
        @NotBlank
        private String razorpayPaymentId;
        @NotBlank
        private String razorpaySignature;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SessionResponse {
        private Long id;
        private Long userId;
        private TrainerResponse trainer;
        private PetDto.PetResponse pet;
        private LocalDate sessionDate;
        private LocalTime sessionTime;
        private Integer durationHours;
        private String mode;
        private String focusArea;
        private String petCurrentIssues;
        private String status;
        private BigDecimal totalFee;
        private String trainerNotes;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserPackageResponse {
        private Long id;
        private TrainingPackageDto packageInfo;
        private String trainerName;
        private Integer sessionsRemaining;
        private LocalDate expiresAt;
        private LocalDateTime purchasedAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VerifyPackageRequest {
        @NotBlank
        private String razorpayOrderId;
        @NotBlank
        private String razorpayPaymentId;
        @NotBlank
        private String razorpaySignature;
    }
}
