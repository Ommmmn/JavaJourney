package com.petymate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "pety_vet_appointments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VetAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id", nullable = false)
    private Vet vet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ONLINE','CLINIC')")
    private AppointmentMode mode;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED')")
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "vet_notes", columnDefinition = "TEXT")
    private String vetNotes;

    @Column(name = "total_fee")
    private BigDecimal totalFee;

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

    public enum AppointmentMode { ONLINE, CLINIC }
    public enum AppointmentStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED }
}
