package com.magicrepokit.auth.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后期可扩展数据库存储
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthAccessToken implements Serializable {
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     *
     */
    private String userType;
    /**
     * 客户端编号
     *
     */
    private String clientId;
    /**
     * 授权范围
     */
    private List<String> scopes;

    /**
     * accessToken过期时间
     */
    private Integer accessTokenExpireTime;

    /**
     * refreshToken过期时间
     */
    private Integer refreshTokenExpireTime;

    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;

}
