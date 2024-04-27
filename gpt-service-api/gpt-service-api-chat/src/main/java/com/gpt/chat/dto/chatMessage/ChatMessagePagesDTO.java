package com.gpt.chat.dto.chatMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class ChatMessagePagesDTO implements Serializable {
    private List<Item> items;
    private Integer total;
    private Integer limit;
    private Integer offset;
    @JsonProperty("has_missing_conversations")
    private Boolean hasMissingConversations;
    @Data
    public static class Item {
        private String id;
        private String title;
        @JsonProperty("create_time")
        private String createTime;
        @JsonProperty("update_time")
        private String updateTime;
        private String mapping;
        @JsonProperty("current_node")
        private String currentNode;
        @JsonProperty("conversation_template_id")
        private String conversationTemplateId;
        @JsonProperty("gizmo_id")
        private String gizmoId;
    }
}
