package com.magicrepokit.user.service;

import com.magicrepokit.mp.base.BaseService;
import com.magicrepokit.user.entity.AuthClient;

import java.util.Collection;

public interface IOAuth2ClientService extends BaseService<AuthClient> {
    /**
     * 从缓存中，校验客户端是否合法
     *
     * 非空时，进行校验
     *
     * @param clientId 客户端编号
     * @param clientSecret 客户端密钥
     * @param authorizedGrantType 授权方式
     * @param scopes 授权范围
     * @param redirectUri 重定向地址
     * @return 客户端
     */
    AuthClient validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType,
                                         Collection<String> scopes, String redirectUri);

    /**
     * 获取客户端信息
     *
     * @param clientId
     * @return
     */
    AuthClient infoClient(String clientId);
}
