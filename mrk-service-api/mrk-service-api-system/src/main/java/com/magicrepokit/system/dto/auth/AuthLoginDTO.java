package com.magicrepokit.system.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginDTO {

    @NotEmpty(message = "The account cannot be empty.")
    private String username;

    @NotEmpty(message = "The password cannot be empty.")
    private String password;
}
