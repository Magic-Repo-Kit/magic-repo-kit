package com.magicrepokit.system.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 三方授权码快捷登录
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuthSocialLoginDTO {

    /**
     * 社交类型
     */
    @NotNull(message = "社交平台的类型不能为空")
    private Integer type;

    /**
     * 三方授权码
     */
    @NotEmpty(message = "授权码不能为空")
    private String code;

    /**
     * 三方state
     */
    @NotEmpty(message = "state 不能为空")
    private String state;
}
