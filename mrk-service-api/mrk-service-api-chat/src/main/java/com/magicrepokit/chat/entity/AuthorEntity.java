package com.magicrepokit.chat.entity;

import lombok.Data;

@Data
public class AuthorEntity {
    private String role;
    private String name;
    private MetadataEntity metadata;
}
