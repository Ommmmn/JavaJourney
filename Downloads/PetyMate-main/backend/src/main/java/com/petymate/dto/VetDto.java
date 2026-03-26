package com.petymate.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class VetDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VetResponse {
        private Long id;
        private String name;
        private String specialization;
        private String qualification;
        private Integer experienceYears;
        private String city;
        private String state;
        private String phone;
        private String email;
        private String photoUrl;
        private BigDecimal consultationFee;
        private String availableDays;
        private String availableHours;
        private BigDecimal rating;
        private Integer reviewCount;
        private Integer totalAppointments;
        private Boolean isVerified;
        private String bio;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateAppointmentOrderRequest {
        @NotNull
        private Long vetId;
        @NotNull
        private Long petId;
        @NotNull
        private LocalDate date;
        @NotNull
        private LocalTime time;
        @NotBlank
        private String mode;
        private String notes;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AppointmentOrderResponse {
        private String razorpayOrderId;
        private int amount;
        private String currency;
        private String keyId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VerifyAppointmentRequest {
        @NotBlank
        private String razorpayOrderId;
        @NotBlank
        private String razorpayPaymentId;
        @NotBlank
        private String razorpaySignature;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AppointmentResponse {
        private Long id;
        private Long userId;
        private VetResponse vet;
        private PetDto.PetResponse pet;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String mode;
        private String status;
        private String notes;
        private String vetNotes;
        private BigDecimal totalFee;
        private LocalDateTime createdAt;
    }
}
