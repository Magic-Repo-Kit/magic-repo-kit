package com.gpt.jwt.properties;

import com.gpt.jwt.constant.JWTConstant;
import io.jsonwebtoken.JwtException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("gpt.token")
public class JWTProperties {
    /**
     * token签名
     */
    private String singKey;

    /**
     * accessToken过期时间
     */

    private int accessTokenValiditySeconds;

    /**
     * refreshToken过期时间
     */
    private int refreshTokenValiditySeconds;

    public String getSingKey(){
        if(this.singKey==null||this.singKey.length()< JWTConstant.SECRET_KEY_LENGTH){
            throw new JwtException("jwt密钥长度不小于"+JWTConstant.SECRET_KEY_LENGTH);
        }
        return this.singKey;
    }

    public int getAccessTokenValiditySeconds(){
        if (accessTokenValiditySeconds==0){
            return JWTConstant.ACCESS_TOKEN_VALIDITY_SECONDS;
        }
        return accessTokenValiditySeconds;
    }

    public int getRefreshTokenValiditySeconds(){
        if(refreshTokenValiditySeconds==0){
            return JWTConstant.REFRESH_TOKEN_VALIDITY_SECONDS;
        }
        return accessTokenValiditySeconds;
    }

}
