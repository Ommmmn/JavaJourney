package com.petymate.dto;

import lombok.*;
import java.util.List;

public class ChatbotDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class MessageRequest {
        private String sessionToken;
        private String message;
        private Long userId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MessageResponse {
        private String sessionToken;
        private String reply;
        private String intent;
        private List<SuggestedAction> suggestedActions;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SuggestedAction {
        private String label;
        private String url;
    }
}
