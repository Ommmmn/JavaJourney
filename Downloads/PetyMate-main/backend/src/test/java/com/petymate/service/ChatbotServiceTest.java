package com.petymate.service;

import com.petymate.dto.ChatbotDto;
import com.petymate.repository.ChatbotSessionRepository;
import com.petymate.repository.UserRepository;
import com.petymate.service.impl.ChatbotServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatbotService Tests")
class ChatbotServiceTest {

    @InjectMocks private ChatbotServiceImpl chatbotService;
    @Mock private ChatbotSessionRepository sessionRepo;
    @Mock private UserRepository userRepository;

    @Test @DisplayName("Hello intent")
    void processMessage_Hello() {
        when(sessionRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        ChatbotDto.MessageRequest req = new ChatbotDto.MessageRequest(null, "Hello!", null);
        ChatbotDto.MessageResponse resp = chatbotService.processMessage(req);
        assertEquals("hello", resp.getIntent());
        assertNotNull(resp.getSessionToken());
        assertTrue(resp.getReply().contains("PetyBot"));
    }

    @Test @DisplayName("Mating intent")
    void processMessage_Mating() {
        when(sessionRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        ChatbotDto.MessageResponse resp = chatbotService.processMessage(new ChatbotDto.MessageRequest(null, "I want to find a mate for my dog", null));
        assertEquals("mating", resp.getIntent());
        assertTrue(resp.getReply().contains("Matching"));
    }

    @Test @DisplayName("Vet intent")
    void processMessage_Vet() {
        when(sessionRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        ChatbotDto.MessageResponse resp = chatbotService.processMessage(new ChatbotDto.MessageRequest(null, "My pet is sick, need a vet", null));
        assertEquals("vet", resp.getIntent());
        assertTrue(resp.getReply().contains("Vet"));
    }

    @Test @DisplayName("Subscription intent")
    void processMessage_Subscription() {
        when(sessionRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        ChatbotDto.MessageResponse resp = chatbotService.processMessage(new ChatbotDto.MessageRequest(null, "What are the subscription plans?", null));
        assertEquals("subscription", resp.getIntent());
    }

    @Test @DisplayName("Products intent")
    void processMessage_Products() {
        when(sessionRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        ChatbotDto.MessageResponse resp = chatbotService.processMessage(new ChatbotDto.MessageRequest(null, "I need dog food", null));
        assertEquals("products", resp.getIntent());
    }

    @Test @DisplayName("Trainer intent")
    void processMessage_Trainer() {
        when(sessionRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        ChatbotDto.MessageResponse resp = chatbotService.processMessage(new ChatbotDto.MessageRequest(null, "I need obedience training for my dog", null));
        assertEquals("trainer", resp.getIntent());
    }
}
