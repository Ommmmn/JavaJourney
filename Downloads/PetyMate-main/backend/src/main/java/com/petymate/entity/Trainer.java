package com.petymate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pety_trainers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainerSpecialization specialization;

    @Column(name = "species_expertise", length = 200)
    private String speciesExpertise;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 300)
    private String certification;

    @Column(columnDefinition = "TEXT")
    private String bio;

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

    @Column(name = "session_fee_per_hour")
    private BigDecimal sessionFeePerHour;

    @Column(name = "session_modes", length = 200)
    private String sessionModes;

    @Column(name = "available_days", length = 100)
    private String availableDays;

    @Column(name = "available_hours", length = 50)
    private String availableHours;

    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "total_sessions")
    @Builder.Default
    private Integer totalSessions = 0;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum TrainerSpecialization {
        OBEDIENCE, AGILITY, BEHAVIOR_CORRECTION, PUPPY_TRAINING,
        TRICK_TRAINING, THERAPY_DOG, PROTECTION, MULTI
    }
}
