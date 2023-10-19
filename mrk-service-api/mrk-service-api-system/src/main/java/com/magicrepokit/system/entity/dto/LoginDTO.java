package com.magicrepokit.system.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotEmpty(message = "The account cannot be empty.")
    private String username;

    @NotEmpty(message = "The password cannot be empty.")
    private String password;
}
