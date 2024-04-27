package com.gpt.chat.dto.chatMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageDTO {
    private String id;
    private AuthorDTO author;
    @JsonProperty("create_time")
    private double createTime;
    @JsonProperty("update_time")
    private Double updateTime;
    @JsonProperty("content")
    private ContentDTO contentDTO;
    private String status;
    @JsonProperty("end_turn")
    private String endTurn;
    private double weight;
    @JsonProperty("metadata")
    private MetadataDTO metadataDTO;
    private String recipient;
}
