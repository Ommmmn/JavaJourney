package com.petymate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pety_match_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_pet_id", nullable = false)
    private Pet requesterPet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_pet_id", nullable = false)
    private Pet receiverPet;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING','ACCEPTED','REJECTED')")
    @Builder.Default
    private MatchStatus status = MatchStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Builder.Default
    private Boolean unlocked = false;

    @Column(name = "unlock_payment_id", length = 200)
    private String unlockPaymentId;

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

    public enum MatchStatus { PENDING, ACCEPTED, REJECTED }
}
