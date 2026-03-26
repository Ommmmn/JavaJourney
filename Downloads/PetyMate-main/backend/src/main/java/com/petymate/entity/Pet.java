package com.petymate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pety_pets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('DOG','CAT','RABBIT','BIRD','FISH','HAMSTER','REPTILE','OTHER')")
    private Species species;

    @Column(length = 100)
    private String breed;

    @Column(name = "age_months")
    private Integer ageMonths;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('MALE','FEMALE')")
    private Gender gender;

    @Column(length = 50)
    private String color;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "vaccination_status")
    @Builder.Default
    private Boolean vaccinationStatus = false;

    @Builder.Default
    private Boolean neutered = false;

    @Column(name = "pedigree_certified")
    @Builder.Default
    private Boolean pedigreeCertified = false;

    @Column(name = "has_own_space")
    @Builder.Default
    private Boolean hasOwnSpace = false;

    @Column(name = "health_status", columnDefinition = "TEXT")
    private String healthStatus;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", columnDefinition = "ENUM('MATING','SALE','ADOPTION')")
    private ListingType listingType;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ACTIVE','INACTIVE','SOLD')")
    @Builder.Default
    private PetStatus status = PetStatus.ACTIVE;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PetPhoto> photos = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Species { DOG, CAT, RABBIT, BIRD, FISH, HAMSTER, REPTILE, OTHER }
    public enum Gender { MALE, FEMALE }
    public enum ListingType { MATING, SALE, ADOPTION }
    public enum PetStatus { ACTIVE, INACTIVE, SOLD }
}
