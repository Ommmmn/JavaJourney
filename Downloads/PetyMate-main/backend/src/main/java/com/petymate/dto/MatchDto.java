package com.petymate.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MatchDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class MatchRequestDto {
        @NotNull(message = "Requester pet ID is required")
        private Long requesterPetId;
        @NotNull(message = "Receiver pet ID is required")
        private Long receiverPetId;
        private String message;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MatchResponse {
        private Long id;
        private Long requesterId;
        private String requesterName;
        private Long receiverId;
        private String receiverName;
        private PetDto.PetResponse requesterPet;
        private PetDto.PetResponse receiverPet;
        private String status;
        private String message;
        private Boolean unlocked;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UnlockRequest {
        @NotBlank
        private String razorpayOrderId;
        @NotBlank
        private String razorpayPaymentId;
        @NotBlank
        private String razorpaySignature;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UnlockOrderResponse {
        private String orderId;
        private int amount;
        private String currency;
        private String keyId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OwnerContactResponse {
        private String ownerName;
        private String ownerPhone;
        private String ownerEmail;
        private String ownerCity;
    }
}
