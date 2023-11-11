package com.magicrepokit.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContentEntity {
    @JsonProperty("content_type")
    private String contentType;
    private List<String> parts;
}
