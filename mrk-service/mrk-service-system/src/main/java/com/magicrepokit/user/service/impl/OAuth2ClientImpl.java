package com.magicrepokit.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.common.enums.CommonStatusEnum;
import com.magicrepokit.common.exception.ServiceException;
import com.magicrepokit.common.utils.StringUtil;
import com.magicrepokit.mp.base.BaseServiceImpl;
import com.magicrepokit.user.constant.MRKSystemConstant;
import com.magicrepokit.user.constant.MRKSystemResultCode;
import com.magicrepokit.user.entity.OAuth2Client;
import com.magicrepokit.user.mapper.OAuth2ClientMapper;
import com.magicrepokit.user.service.IOAuth2ClientService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class OAuth2ClientImpl  extends BaseServiceImpl<OAuth2ClientMapper, OAuth2Client> implements IOAuth2ClientService {
    @Override
    public OAuth2Client validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType, Collection<String> scopes, String redirectUri) {
        OAuth2Client client = getSelf().getOAuth2ClientFromCache(clientId);
        if(ObjectUtil.isEmpty(client)){
            throw new ServiceException(MRKSystemResultCode.OAUTH2_CLIENT_NOT_EXISTS);
        }
        if(ObjectUtil.notEqual(client.getStatus(), CommonStatusEnum.ENABLE.getStatus())){
            throw new ServiceException(MRKSystemResultCode.OAUTH2_CLIENT_DISABLE);
        }
        //校验密钥
        if(StrUtil.isEmpty(clientSecret)||!BCrypt.checkpw(clientSecret,client.getSecret())){
            throw new ServiceException(MRKSystemResultCode.OAUTH2_CLIENT_CLIENT_SECRET_ERROR);
        }
        // 校验授权方式
        if (StrUtil.isNotEmpty(authorizedGrantType) && !CollUtil.contains(client.getAuthorizedGrantTypes(), authorizedGrantType)) {
            throw new ServiceException(MRKSystemResultCode.OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS);
        }
        // 校验授权范围
        if (CollUtil.isNotEmpty(scopes) && !CollUtil.containsAll(client.getScopes(), scopes)) {
            throw new ServiceException(MRKSystemResultCode.OAUTH2_CLIENT_SCOPE_OVER);
        }
        // 校验回调地址
        if (StrUtil.isNotEmpty(redirectUri) && !StringUtil.startWithAny(redirectUri, client.getRedirectUris())) {
            throw new ServiceException(MRKSystemResultCode.OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH, redirectUri);
        }
        return client;
    }

    /**
     * 从缓存获取客户段
     *
     * @param clientId 客户端id
     * @return 客户端
     */
    @Cacheable(cacheNames = MRKSystemConstant.CACHE_NAME_OAUTH_CLIENT, key = "#clientId",
            unless = "#result == null")
    public OAuth2Client getOAuth2ClientFromCache(String clientId) {
        return this.getOne(new LambdaQueryWrapper<OAuth2Client>()
                .eq(OAuth2Client::getClientId,clientId)
        );
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private OAuth2ClientImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
