package com.petymate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pety_user_packages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private TrainingPackage trainingPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "sessions_remaining", nullable = false)
    private Integer sessionsRemaining;

    @Column(name = "razorpay_payment_id", length = 200)
    private String razorpayPaymentId;

    @Column(name = "expires_at")
    private LocalDate expiresAt;

    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @PrePersist
    protected void onCreate() {
        purchasedAt = LocalDateTime.now();
    }
}
