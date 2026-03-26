package com.petymate.repository;

import com.petymate.entity.VetAppointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface VetAppointmentRepository extends JpaRepository<VetAppointment, Long> {
    Page<VetAppointment> findByUserIdOrderByAppointmentDateDesc(Long userId, Pageable pageable);

    boolean existsByVetIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long vetId, LocalDate date, LocalTime time, VetAppointment.AppointmentStatus status);

    Optional<VetAppointment> findByRazorpayOrderId(String razorpayOrderId);
}
