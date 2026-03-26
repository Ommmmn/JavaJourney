package com.petymate.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ProductResponse {
        private Long id;
        private String name;
        private String category;
        private String speciesTags;
        private String description;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer stockQty;
        private String brand;
        private String photoUrl;
        private BigDecimal rating;
        private Integer reviewCount;
        private Boolean isFeatured;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ReviewRequest {
        @NotNull @Min(1) @Max(5)
        private Integer rating;
        private String comment;
    }
}
