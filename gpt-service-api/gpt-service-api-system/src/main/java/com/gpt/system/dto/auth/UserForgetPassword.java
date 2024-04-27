package com.gpt.system.dto.auth;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "根据邮箱修改密码DTO", description = "根据邮箱修改密码")
public class UserForgetPassword {
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
