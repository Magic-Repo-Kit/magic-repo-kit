package com.magicrepokit.auth.support;

import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.auth.service.MrkUserDetails;
import com.magicrepokit.common.utils.WebUtil;
import com.magicrepokit.jwt.constant.JWTConstant;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 令牌增强器
 */
@AllArgsConstructor
public class MRKJwtTokenEnhancer implements TokenEnhancer {
    private final JwtAccessTokenConverter jwtAccessTokenConverter;
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        if(oAuth2Authentication.getUserAuthentication()==null){
            return oAuth2AccessToken;
        }
        //oauth中user信息
        MrkUserDetails principal = (MrkUserDetails) oAuth2Authentication.getUserAuthentication().getPrincipal();
        //token参数增强
        Map<String, Object> info = new HashMap<>(16);
        HttpServletRequest request = WebUtil.getRequest();
        info.put(JWTConstant.USER_ID, principal.getUserId());
        info.put(JWTConstant.DEPT_ID, principal.getDeptId());
        info.put(JWTConstant.POST_ID, principal.getPostId());
        info.put(JWTConstant.ROLE_ID, principal.getRoleId());
        info.put(JWTConstant.ACCOUNT, principal.getAccount());
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);


        return oAuth2AccessToken;
    }
}
