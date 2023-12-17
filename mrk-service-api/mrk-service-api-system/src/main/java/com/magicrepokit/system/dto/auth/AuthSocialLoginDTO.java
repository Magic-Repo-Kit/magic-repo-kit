package com.magicrepokit.system.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "三方授权码快捷登录", description = "三方授权码快捷登录")
public class AuthSocialLoginDTO {

    /**
     * 社交类型
     */
    @NotNull(message = "社交平台的类型不能为空")
    @ApiModelProperty(value = "社交平台枚举[10：github 20:google 30:gitee]", required = true)
    private Integer type;

    /**
     * 三方授权码
     */
    @NotEmpty(message = "授权码不能为空")
    @ApiModelProperty(value = "三方授权码", required = true)
    private String code;

    /**
     * 三方state
     */
    @NotEmpty(message = "state 不能为空")
    @ApiModelProperty(value = "三方state", required = true)
    private String state;
}
