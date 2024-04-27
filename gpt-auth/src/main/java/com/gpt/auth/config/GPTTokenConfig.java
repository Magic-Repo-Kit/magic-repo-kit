package com.gpt.auth.config;

import com.gpt.auth.support.GPTJwtTokenEnhancer;
import com.magicrepokit.jwt.properties.JWTProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class GPTTokenConfig {
    /**
     * 指定令牌存储
     * @return
     */
    @Bean
    public TokenStore tokenStore(JWTProperties jwtProperties){
        return new JwtTokenStore(accessTokenConverter(jwtProperties));
    }


    /**
     * 指定jwt密钥
     * @param jwtProperties
     * @return
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter(JWTProperties jwtProperties){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        //设置jwt密钥
        converter.setSigningKey(jwtProperties.getSingKey());
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer(JwtAccessTokenConverter accessTokenConverter){
        return new GPTJwtTokenEnhancer(accessTokenConverter);
    }

    @Bean
    public GPTOAuthRequestFactory mrkOAuthRequestFactory(ClientDetailsService clientDetailsService){
        return new GPTOAuthRequestFactory(clientDetailsService);
    }
}
