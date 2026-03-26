package com.petymate.repository;

import com.petymate.entity.ChatbotSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChatbotSessionRepository extends JpaRepository<ChatbotSession, Long> {
    Optional<ChatbotSession> findBySessionToken(String sessionToken);
}
