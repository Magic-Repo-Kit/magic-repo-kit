package com.magicrepokit.auth.support;

import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.auth.service.MrkUserDetails;
import com.magicrepokit.common.utils.WebUtil;
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
        info.put(MRKAuthConstant.CLIENT_ID, "666666");
        info.put(MRKAuthConstant.USER_ID, "principal.getUserId()");
        info.put(MRKAuthConstant.DEPT_ID, "88888888888888");
        info.put(MRKAuthConstant.POST_ID, "99999999999");
        info.put(MRKAuthConstant.ROLE_ID, "principal.getRoleId()");
        info.put(MRKAuthConstant.ACCOUNT, "principal.getAccount()");
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);


        return oAuth2AccessToken;
    }
}
