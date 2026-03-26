package com.petymate.repository;

import com.petymate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);

    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:tier IS NULL OR u.subscriptionTier = :tier) AND " +
           "(:banned IS NULL OR u.isBanned = :banned)")
    Page<User> findAllFiltered(User.Role role, User.SubscriptionTier tier, Boolean banned, Pageable pageable);

    long countByIsBannedFalse();

    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = CURRENT_DATE")
    long countTodayRegistrations();
}
