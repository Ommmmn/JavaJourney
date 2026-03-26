package com.petymate.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DashboardResponse {
        private long totalUsers;
        private long activeUsers;
        private long todayRegistrations;
        private long totalPets;
        private long activePetListings;
        private long totalMatches;
        private long successfulMatches;
        private long totalOrders;
        private long todayOrders;
        private BigDecimal monthlyRevenue;
        private BigDecimal totalRevenue;
        private long pendingVetVerifications;
        private long pendingTrainerVerifications;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UpdateOrderStatusRequest {
        private String status;
        private String trackingNumber;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UpdateTierRequest {
        private String tier;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReviewResponse {
        private Long id;
        private Long reviewerId;
        private String reviewerName;
        private String targetType;
        private Long targetId;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }
}
