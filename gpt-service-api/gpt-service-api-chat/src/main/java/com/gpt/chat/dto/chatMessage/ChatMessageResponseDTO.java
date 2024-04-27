package com.gpt.chat.dto.chatMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ChatMessageResponseDTO implements Serializable {
    @JsonProperty("message")
    private MessageDTO messageDTO;
    @JsonProperty("conversation_id")
    private String conversationId;
    private String error;
    @JsonProperty("message_id")
    private String messageId;
    @JsonProperty("is_completion")
    private Boolean isCompletion;
    @JsonProperty("moderation_response")
    private ModerationResponse moderationResponse;
    private Boolean done;

    @Data
    public static class ModerationResponse {
        private Boolean flagged;
        private Boolean blocked;
        @JsonProperty("moderation_id")
        private String moderationId;
    }
}
