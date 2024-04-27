package com.gpt.chat.dto.chatMessage;

import lombok.Data;

@Data
public class AuthorDTO {
    private String role;
    private String name;
    private MetadataDTO metadataDTO;
}
