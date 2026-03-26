package com.petymate.repository;

import com.petymate.entity.TrainingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    Page<TrainingSession> findByUserIdOrderBySessionDateDesc(Long userId, Pageable pageable);

    boolean existsByTrainerIdAndSessionDateAndSessionTimeAndStatusNot(
            Long trainerId, LocalDate date, LocalTime time, TrainingSession.SessionStatus status);

    Optional<TrainingSession> findByRazorpayOrderId(String razorpayOrderId);
}
