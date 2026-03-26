package com.petymate.repository;

import com.petymate.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findTopByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Subscription.SubscriptionStatus status);
    Optional<Subscription> findByRazorpayOrderId(String razorpayOrderId);
    List<Subscription> findByUserIdOrderByCreatedAtDesc(Long userId);
}
