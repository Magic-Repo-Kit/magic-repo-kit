package com.magicrepokit.auth.service;


import com.magicrepokit.auth.entity.AuthAccessToken;
import com.magicrepokit.user.entity.AuthClient;
import com.magicrepokit.user.vo.UserInfo;

import java.util.List;

/**
 * OAuth2.0 的令牌服务
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
public interface MRKAuthTokenService {
    /**
     * 创建访问令牌
     * 注意：该流程中，会包含创建刷新令牌的创建
     *
     * 参考 DefaultTokenServices 的 createAccessToken 方法
     *
     * @param user 用户信息
     * @param client 客户端信息
     * @param scopes 授权范围
     * @return 访问令牌的信息
     */
    AuthAccessToken createAccessToken(UserInfo user, AuthClient client, List<String> scopes,String userType);

    /**
     * 刷新访问令牌
     *
     * 参考 DefaultTokenServices 的 refreshAccessToken 方法
     *
     * @param refreshToken 刷新令牌
     * @return 访问令牌的信息
     */
    AuthAccessToken refreshAccessToken(String refreshToken,String userType);

    /**
     * 移除访问令牌
     * 注意：该流程中，会移除相关的刷新令牌
     *
     * 参考 DefaultTokenServices 的 revokeToken 方法
     *
     * @param accessToken 刷新令牌
     * @return 访问令牌的信息
     */
    AuthAccessToken removeAccessToken(String accessToken,String userType);
}
