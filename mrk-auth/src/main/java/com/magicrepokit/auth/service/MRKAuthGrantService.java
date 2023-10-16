package com.magicrepokit.auth.service;

import com.magicrepokit.auth.entity.AuthAccessToken;
import com.magicrepokit.user.entity.AuthClient;

import java.util.List;

/**
 * oauth2 认证服务
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
public interface MRKAuthGrantService {

    /**
     * 校验客户端信息
     *
     * @param clientId
     * @param clientSecret
     * @param grantType
     * @param scopes
     * @param redirectUri
     * @return
     */
    AuthClient validOAuthClient(String clientId, String clientSecret,String grantType,List<String> scopes,String redirectUri);

    /**
     * 密码模式
     *
     * 对应 Spring Security OAuth2 的 ResourceOwnerPasswordTokenGranter 功能
     *
     * @param username 账号
     * @param password 密码
     * @param clientId 客户端编号
     * @param scopes 授权范围
     * @return 访问令牌
     */
   AuthAccessToken grantPassword(String username, String password,
                                 String clientId, List<String> scopes,String userType);

    /**
     * 授权码模式，获得 accessToken 访问令牌
     *
     * 对应 Spring Security OAuth2 的 AuthorizationCodeTokenGranter 功能
     *
     * @param clientId 客户端编号
     * @param code 授权码
     * @param redirectUri 重定向 URI
     * @param state 状态
     * @return 访问令牌
     */
    AuthAccessToken grantAuthorizationCodeForAccessToken(String clientId, String code,
                                                             String redirectUri, String state,String userType);

    /**
     * 刷新模式
     *
     * 对应 Spring Security OAuth2 的 ResourceOwnerPasswordTokenGranter 功能
     *
     * @param refreshToken 刷新令牌
     * @param clientId 客户端编号
     * @return 访问令牌
     */
    AuthAccessToken grantRefreshToken(String refreshToken, String clientId,String userType);

    /**
     * 获取的授权码
     *
     * 对应 Spring Security OAuth2 的 AuthorizationEndpoint 的 generateCode 方法
     *
     * @param userId 用户编号
     * @param userType 用户类型
     * @param clientId 客户端编号
     * @param scopes 授权范围
     * @param redirectUri 重定向 URI
     * @param state 状态
     * @return 授权码
     */
    String grantAuthorizationCodeForCode(Long userId, Integer userType,
                                         String clientId, List<String> scopes,
                                         String redirectUri, String state);
}
