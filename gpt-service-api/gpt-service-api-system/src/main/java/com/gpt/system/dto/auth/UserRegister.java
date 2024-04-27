package com.gpt.system.dto.auth;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "注册对象DTO", description = "注册对象")
public class UserRegister {
    /**
     * 账户
     */
    @ApiModelProperty(value = "账户", required = true)
    private String account;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码", required = true)
    private String verificationCode;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    /**
     * 确认密码
     */
    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPassword;
}
