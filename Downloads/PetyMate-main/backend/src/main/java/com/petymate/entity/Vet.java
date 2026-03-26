package com.petymate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pety_vets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 200)
    private String specialization;

    @Column(length = 300)
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(length = 15)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "consultation_fee")
    private BigDecimal consultationFee;

    @Column(name = "available_days", length = 100)
    private String availableDays;

    @Column(name = "available_hours", length = 50)
    private String availableHours;

    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "total_appointments")
    @Builder.Default
    private Integer totalAppointments = 0;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
