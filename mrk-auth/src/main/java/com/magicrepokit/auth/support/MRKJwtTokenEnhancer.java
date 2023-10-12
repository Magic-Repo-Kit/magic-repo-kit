package com.magicrepokit.auth.support;

import com.magicrepokit.user.entity.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * 令牌增强器
 */
public class MRKJwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        if(oAuth2Authentication.getUserAuthentication()==null){
            return oAuth2AccessToken;
        }
        //oauth中user信息
        User principal = (User) oAuth2Authentication.getUserAuthentication().getPrincipal();
        return null;
    }
}
