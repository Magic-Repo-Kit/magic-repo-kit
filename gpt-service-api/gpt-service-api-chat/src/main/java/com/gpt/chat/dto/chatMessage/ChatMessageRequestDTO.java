package com.gpt.chat.dto.chatMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChatMessageRequestDTO {
    private String action;
    private String arkoseToken;
    private List<Message> messages;
    @JsonProperty("parent_message_id")
    private String parentMessageId;
    private String model;
    @JsonProperty("timezone_offset_min")
    private int timezoneOffsetMin;
    @JsonProperty("history_and_training_disabled")
    private boolean historyAndTrainingDisabled;
    @JsonProperty("conversation_mode")
    private ConversationMode conversationMode;
    @JsonProperty("force_paragen")
    private boolean forceParagen;
    @JsonProperty("force_rate_limit")
    private boolean forceRateLimit;
    @Data
    public static class Message {
        private String id;
        private Author author;
        private Content content;
        private MetadataDTO metadataDTO;
    }

    @Data
    public static class Author {
        private String role;
    }

    @Data
    public static class Content {
        @JsonProperty("content_type")
        private String contentType;
        private List<String> parts;
    }

    @Data
    public static class ConversationMode {
        private String kind;
    }
}
