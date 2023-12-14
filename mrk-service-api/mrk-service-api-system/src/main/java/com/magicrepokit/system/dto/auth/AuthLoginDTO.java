package com.magicrepokit.system.dto.auth;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "登录对象DTO", description = "登录对象")
public class AuthLoginDTO {

    @NotEmpty(message = "The account cannot be empty.")
    @ApiModelProperty(value = "账号", required = true)
    private String username;

    @NotEmpty(message = "The password cannot be empty.")
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
