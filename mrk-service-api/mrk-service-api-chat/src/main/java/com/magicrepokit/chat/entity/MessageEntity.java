package com.magicrepokit.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageEntity {
    private String id;
    private AuthorEntity author;
    @JsonProperty("create_time")
    private double createTime;
    @JsonProperty("update_time")
    private Double updateTime;
    private ContentEntity content;
    private String status;
    @JsonProperty("end_turn")
    private String endTurn;
    private double weight;
    private MetadataEntity metadata;
    private String recipient;
}
