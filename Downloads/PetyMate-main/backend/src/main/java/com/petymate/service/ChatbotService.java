package com.petymate.service;

import com.petymate.dto.ChatbotDto;

public interface ChatbotService {
    ChatbotDto.MessageResponse processMessage(ChatbotDto.MessageRequest request);
}
