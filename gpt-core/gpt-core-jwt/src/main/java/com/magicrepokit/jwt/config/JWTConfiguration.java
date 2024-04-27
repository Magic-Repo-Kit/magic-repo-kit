package com.magicrepokit.jwt.config;


import com.magicrepokit.jwt.properties.JWTProperties;
import com.magicrepokit.jwt.utils.JWTUtil;
import com.magicrepokit.redis.utils.GPTRedisUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@EnableConfigurationProperties({JWTProperties.class})
@Configuration
public class JWTConfiguration implements SmartInitializingSingleton {
    @Autowired
    private  JWTProperties jwtProperties;
    @Autowired
    private GPTRedisUtils GPTRedisUtils;

    @Override
    public void afterSingletonsInstantiated() {
        JWTUtil.setJwtProperties(jwtProperties);
        JWTUtil.setMRKRedisUtils(GPTRedisUtils);
    }
}
