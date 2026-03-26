package com.petymate.repository;

import com.petymate.entity.MatchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    Page<MatchRequest> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);
    Page<MatchRequest> findByRequesterIdOrderByCreatedAtDesc(Long requesterId, Pageable pageable);

    boolean existsByRequesterPetIdAndReceiverPetId(Long requesterPetId, Long receiverPetId);

    @Query("SELECT COUNT(m) FROM MatchRequest m WHERE m.requester.id = :userId AND m.createdAt >= :since")
    long countRequestsSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    long countByStatus(MatchRequest.MatchStatus status);

    @Query("SELECT COUNT(m) FROM MatchRequest m WHERE m.status = 'ACCEPTED' AND m.unlocked = true")
    long countSuccessfulMatches();

    Page<MatchRequest> findByReceiverIdAndStatus(Long receiverId, MatchRequest.MatchStatus status, Pageable pageable);
}
