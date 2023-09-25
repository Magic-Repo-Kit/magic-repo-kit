package com.magicrepokit.auth.service;

import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * MRK授权码管理服务
 */
@Service
public class MRKAuthorizationCodeServiceImpl extends RandomValueAuthorizationCodeServices {
    @Autowired
    private MRKRedisUtils redisUtils;
    private final RedisSerializer<Object> valueSerializer;

    public MRKAuthorizationCodeServiceImpl() {
        this.valueSerializer = RedisSerializer.java();
    }

    /**
     * 塞入redis缓存中 过期10分钟
     * @param code
     * @param oAuth2Authentication
     */
    @Override
    protected void store(String code, OAuth2Authentication oAuth2Authentication) {
        redisUtils.setExpire(getKey(code), oAuth2Authentication, 10, TimeUnit.MINUTES, valueSerializer);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        String codeKey = getKey(code);
        OAuth2Authentication token = (OAuth2Authentication) redisUtils.get(codeKey, valueSerializer);
        redisUtils.del(codeKey);
        return token;
    }

    /**
     * 获取key
     *
     * @param code
     * @return key
     */
    private String getKey(String code) {
        return MRKAuthConstant.REDIS_KEY_AUTHORIZATION_CODE_PREFIX + code;
    }
}
