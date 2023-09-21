package com.magicrepokit.jwt.properties;

import com.magicrepokit.jwt.constant.JWTConstant;
import io.jsonwebtoken.JwtException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("mrk.token")
@Component
public class JWTProperties {
    /**
     * token签名
     */
    private String singKey;

    public String getSingKey(){
        if(this.singKey==null||this.singKey.length()< JWTConstant.SECRET_KEY_LENGTH){
            throw new JwtException("mrk的jwt密钥长度不小于"+JWTConstant.SECRET_KEY_LENGTH);
        }
        return this.singKey;
    }
}
