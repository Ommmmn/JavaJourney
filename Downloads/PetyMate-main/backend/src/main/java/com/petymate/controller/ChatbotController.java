package com.petymate.controller;

import com.petymate.dto.ApiResponse;
import com.petymate.dto.ChatbotDto;
import com.petymate.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ApiResponse<ChatbotDto.MessageResponse>> message(
            @RequestBody ChatbotDto.MessageRequest request) {
        return ResponseEntity.ok(ApiResponse.success(chatbotService.processMessage(request)));
    }
}
