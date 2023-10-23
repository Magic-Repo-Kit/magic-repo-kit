package com.magicrepokit.social.config;

import com.magicrepokit.social.factory.MRKAuthRequestFactory;
import com.xingyuv.http.HttpUtil;
import com.xingyuv.http.support.Http;
import com.xingyuv.http.support.httpclient.HttpClientImpl;
import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.justauth.autoconfigure.JustAuthProperties;
import com.xingyuv.justauth.support.cache.RedisStateCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@EnableConfigurationProperties(JustAuthProperties.class)
public class MRKAuthRequestConfiguration {
    //    @Bean
//    @ConditionalOnMissingBean(AuthStateCache.class)
//    public AuthStateCache authStateCache(RedisTemplate<String, Object> redisTemplate) {
//        return new AuthStateRedisCache(redisTemplate, redisTemplate.opsForValue());
//    }
    @Bean
    @ConditionalOnMissingBean(Http.class)
    public Http simpleHttp() {
        HttpClientImpl httpClient = new HttpClientImpl();
        HttpUtil.setHttp(httpClient);
        return httpClient;
    }

    @Bean
    public AuthStateCache setAuthStateCache(RedisTemplate redisTemplate, JustAuthProperties justAuthProperties) {
        return new RedisStateCache(redisTemplate, justAuthProperties.getCache());
    }

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "justauth", value = "enabled", havingValue = "true", matchIfMissing = true)
    public MRKAuthRequestFactory getMRKAuthRequestFactory(JustAuthProperties properties, AuthStateCache authStateCache) {
        // 需要修改 HttpUtil 使用的实现，避免类报错
//        HttpUtil.setHttp(new HutoolImpl());
        return new MRKAuthRequestFactory(properties, authStateCache);
    }
}
