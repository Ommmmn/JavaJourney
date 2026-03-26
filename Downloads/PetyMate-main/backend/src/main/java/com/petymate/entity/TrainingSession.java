package com.petymate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "pety_training_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_time", nullable = false)
    private LocalTime sessionTime;

    @Column(name = "duration_hours")
    @Builder.Default
    private Integer durationHours = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('HOME_VISIT','TRAINING_CENTER','ONLINE','GROUP_CLASS')")
    private SessionMode mode;

    @Column(name = "focus_area", length = 200)
    private String focusArea;

    @Column(name = "pet_current_issues", columnDefinition = "TEXT")
    private String petCurrentIssues;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED')")
    @Builder.Default
    private SessionStatus status = SessionStatus.PENDING;

    @Column(name = "total_fee")
    private BigDecimal totalFee;

    @Column(name = "trainer_notes", columnDefinition = "TEXT")
    private String trainerNotes;

    @Column(name = "razorpay_order_id", length = 200)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 200)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", length = 500)
    private String razorpaySignature;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum SessionMode { HOME_VISIT, TRAINING_CENTER, ONLINE, GROUP_CLASS }
    public enum SessionStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED }
}
