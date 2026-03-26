package com.petymate.repository;

import com.petymate.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(Review.TargetType targetType, Long targetId, Pageable pageable);

    boolean existsByReviewerIdAndTargetTypeAndTargetId(Long reviewerId, Review.TargetType targetType, Long targetId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.targetType = :targetType AND r.targetId = :targetId")
    Double getAverageRating(@Param("targetType") Review.TargetType targetType, @Param("targetId") Long targetId);

    long countByTargetTypeAndTargetId(Review.TargetType targetType, Long targetId);
    
    Page<Review> findByReviewerIdOrderByCreatedAtDesc(Long reviewerId, Pageable pageable);
}
