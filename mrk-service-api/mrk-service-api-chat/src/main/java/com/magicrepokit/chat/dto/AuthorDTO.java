package com.magicrepokit.chat.dto;

import lombok.Data;

@Data
public class AuthorDTO {
    private String role;
    private String name;
    private MetadataDTO metadataDTO;
}
