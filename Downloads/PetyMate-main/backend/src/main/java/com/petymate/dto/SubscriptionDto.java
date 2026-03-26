package com.petymate.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SubscriptionDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateOrderRequest {
        @NotBlank(message = "Plan is required")
        private String plan;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VerifyRequest {
        @NotBlank
        private String razorpayOrderId;
        @NotBlank
        private String razorpayPaymentId;
        @NotBlank
        private String razorpaySignature;
        @NotBlank
        private String plan;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderResponse {
        private String orderId;
        private int amount;
        private String currency;
        private String keyId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SubscriptionResponse {
        private Long id;
        private String plan;
        private String status;
        private String tier;
        private BigDecimal amount;
        private LocalDateTime startedAt;
        private LocalDateTime expiresAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PlanDetail {
        private String name;
        private BigDecimal price;
        private String browsing;
        private String requests;
        private String unlocks;
        private boolean priorityListing;
        private String badge;
        private boolean earlyAccess;
        private List<String> features;
    }
}
