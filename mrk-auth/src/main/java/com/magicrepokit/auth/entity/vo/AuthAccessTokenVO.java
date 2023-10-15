package com.magicrepokit.auth.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * token返回实体
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthAccessTokenVO {

    /**
     * 授权token
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 刷新token
     */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 令牌类型：bearer 或 basic
     */
    @JsonProperty("token_type")
    private String tokenType;


    /**
     * 过期时间
     * 单位：秒
     */
    @JsonProperty("expires_in")
    private Long expiresIn;

    /**
     * 授权范围：如果有多个授权范围，使用空格分割
     */
    private String scope;
}
