package com.magicrepokit.auth.support;

import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.auth.service.MrkUserDetails;
import com.magicrepokit.common.utils.WebUtil;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
        MrkUserDetails principal = (MrkUserDetails) oAuth2Authentication.getUserAuthentication().getPrincipal();
        //token参数增强
        Map<String, Object> info = new HashMap<>(16);
        HttpServletRequest request = WebUtil.getRequest();
        info.put(MRKAuthConstant.CLIENT_ID, request.getParameter(MRKAuthConstant.CLIENT_ID));
        info.put(MRKAuthConstant.USER_ID, principal.getUserId());
        info.put(MRKAuthConstant.DEPT_ID, principal.getDeptId());
        info.put(MRKAuthConstant.POST_ID, principal.getPostId());
        info.put(MRKAuthConstant.ROLE_ID, principal.getRoleId());
        info.put(MRKAuthConstant.ACCOUNT, principal.getAccount());
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);


        return oAuth2AccessToken;
    }
}
