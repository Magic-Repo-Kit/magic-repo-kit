package com.magicrepokit.chat.dto.chatMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContentDTO {
    @JsonProperty("content_type")
    private String contentType;
    private List<String> parts;
}
