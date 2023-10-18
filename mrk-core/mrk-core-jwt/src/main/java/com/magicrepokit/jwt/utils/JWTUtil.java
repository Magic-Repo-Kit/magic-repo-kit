package com.magicrepokit.jwt.utils;

import com.magicrepokit.jwt.constant.JWTConstant;
import com.magicrepokit.jwt.properties.JWTProperties;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * jwt相关的令牌工具类
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
public class JWTUtil {
    /**
     * jwt配置文件
     */
    @Getter
    private static JWTProperties jwtProperties;

    /**
     * redis工具
     */
    @Getter
    private static MRKRedisUtils mrkRedisUtils;

    public static void setJwtProperties(JWTProperties properties) {
        if (JWTUtil.jwtProperties == null) {
            JWTUtil.jwtProperties = properties;
        }
    }

    public static void setMRKRedisUtils(MRKRedisUtils mrkRedisUtils) {
        if (JWTUtil.mrkRedisUtils == null) {
            JWTUtil.mrkRedisUtils = mrkRedisUtils;
        }
    }



    /**
     * 解析jsonWebToken
     *
     * @param jsonWebToken token串
     * @return Claims
     */
    public static Claims parseJWT(String jsonWebToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Base64.getDecoder().decode(getBase64Security())).build()
                    .parseClaimsJws(jsonWebToken).getBody();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 签名加密
     */
    public static String getBase64Security() {;
        return Base64.getEncoder().encodeToString(getJwtProperties().getSingKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取保存在redis的accessToken
     *
     * @param userId 用户id
     * @return accessToken
     */
    public static String getAccessToken(Long userId) {
        if (mrkRedisUtils.hasKey(getAccessTokenRedisKey(userId))) {
            return (String) mrkRedisUtils.get(getAccessTokenRedisKey(userId));
        } else {
            return null;
        }

    }

    /**
     * 获取用户user的accessToken在redis中key
     *
     * @param userId
     * @return
     */
    public static String getAccessTokenRedisKey(Long userId) {
        return JWTConstant.REDIS_KEY_ACCESS_TOKEN + userId;
    }


}
