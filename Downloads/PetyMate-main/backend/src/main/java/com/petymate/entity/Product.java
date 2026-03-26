package com.petymate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pety_products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('FOOD','TOYS','GROOMING','MEDICINE','ACCESSORIES','HOUSING')")
    private ProductCategory category;

    @Column(name = "species_tags", length = 200)
    private String speciesTags;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "stock_qty")
    @Builder.Default
    private Integer stockQty = 0;

    @Column(length = 100)
    private String brand;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum ProductCategory { FOOD, TOYS, GROOMING, MEDICINE, ACCESSORIES, HOUSING }
}
