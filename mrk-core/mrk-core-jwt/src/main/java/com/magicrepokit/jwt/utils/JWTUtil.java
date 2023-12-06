package com.magicrepokit.jwt.utils;

import com.magicrepokit.jwt.constant.JWTConstant;
import com.magicrepokit.jwt.properties.JWTProperties;
import com.magicrepokit.redis.utils.MRKRedisUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * jwt相关的令牌工具类
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
public class JWTUtil {
    private static final String JWT_PREFIX = "Bearer ";


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
    public static String getAccessToken(Long userId,String userType) {
        if (getMrkRedisUtils().hasKey(getAccessTokenRedisKey(userId,userType))) {
            return (String) getMrkRedisUtils().get(getAccessTokenRedisKey(userId,userType));
        } else {
            return null;
        }

    }


    /**
     * 获取保存在redis的refreshToken
     *
     * @param userId
     * @param userType
     * @return refreshToken
     */
    public static String getRefreshToken(Long userId,String userType){
        if (getMrkRedisUtils().hasKey(getRefreshTokenRedisKey(userId,userType))) {
            return (String)getMrkRedisUtils().get(getRefreshTokenRedisKey(userId,userType));
        } else {
            return null;
        }
    }

    /**
     * 添加token状态到redis
     *
     * @param userId
     * @param userType
     * @param accessTokenValue
     */
    public static void addAccessToken(Long userId, String userType, String accessTokenValue) {
        getMrkRedisUtils().del(getAccessTokenRedisKey(userId,userType));
        getMrkRedisUtils().set(getAccessTokenRedisKey(userId,userType),accessTokenValue, getJwtProperties().getAccessTokenValiditySeconds());
    }

    /**
     * 添加token状态到redis
     *
     * @param userId
     * @param userType
     * @param refreshToken
     */
    public static void addRefreshToken(Long userId, String userType, String refreshToken) {
        getMrkRedisUtils().del(getRefreshTokenRedisKey(userId,userType));
        getMrkRedisUtils().set(getRefreshTokenRedisKey(userId,userType),refreshToken, getJwtProperties().getRefreshTokenValiditySeconds());
    }

    /**
     * 获取用户user的accessToken在redis中key
     *
     * @param userId
     * @return
     */
    public static String getAccessTokenRedisKey(Long userId,String userType) {
        return JWTConstant.REDIS_KEY_ACCESS_TOKEN + userType+":"+userId;
    }

    /**
     * 获取用户user的Refresh在redis中key
     *
     * @param userId
     * @return
     */
    public static String getRefreshTokenRedisKey(Long userId,String userType) {
        return JWTConstant.REDIS_KEY_REFRESH_TOKEN + userType+":"+userId;
    }

    public static String getAuthorization(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }


    public static String getToken(String auth) {
        if(auth!=null&&auth.startsWith("Bearer")){
            return auth.substring(7);
        }
        return auth;
    }
}
