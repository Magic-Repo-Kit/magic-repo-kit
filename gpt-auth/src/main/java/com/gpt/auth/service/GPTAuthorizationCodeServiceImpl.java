package com.gpt.auth.service;

import com.gpt.auth.constant.GPTAuthConstant;
import com.gpt.redis.utils.GPTRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 授权码管理服务
 */
@Service
public class GPTAuthorizationCodeServiceImpl extends RandomValueAuthorizationCodeServices {
    @Autowired
    private GPTRedisUtils redisUtils;
    private final RedisSerializer<Object> valueSerializer;

    public GPTAuthorizationCodeServiceImpl() {
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

    /**
     * 验证删除授权码
     * @param code
     * @return
     */
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
        return GPTAuthConstant.REDIS_KEY_AUTHORIZATION_CODE_PREFIX + code;
    }
}
