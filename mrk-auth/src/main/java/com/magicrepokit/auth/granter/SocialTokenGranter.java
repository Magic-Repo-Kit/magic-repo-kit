package com.magicrepokit.auth.granter;

import com.magicrepokit.auth.constant.MRKI18N;
import com.magicrepokit.auth.service.MrkUserDetails;
import com.magicrepokit.common.api.R;
import com.magicrepokit.system.constant.SocialTypeEnum;
import com.magicrepokit.system.dto.auth.AuthSocialLoginDTO;
import com.magicrepokit.system.vo.auth.SocialUserAuthVO;
import com.magicrepokit.system.vo.user.UserInfoVO;
import com.magicrepokit.system.feign.ISystemClient;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SocialTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "social";
    private final ISystemClient ISystemClient;


    protected SocialTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, ISystemClient ISystemClient) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.ISystemClient = ISystemClient;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        // 开放平台来源
        String source = parameters.get("source");
        SocialTypeEnum socialTypeEnum = SocialTypeEnum.valueOfType(Integer.valueOf(source));
        if(socialTypeEnum==null){
            throw new UsernameNotFoundException(MRKI18N.UNKNOWN_SOURCE_TYPE.getMessage());
        }
        // 开放平台授权码
        String code = parameters.get("code");
        // 开放平台状态吗
        String state = parameters.get("state");
        //获取账户平台绑定信息
        AuthSocialLoginDTO authSocialLoginDTO = new AuthSocialLoginDTO(socialTypeEnum.getType(),code,state);
        R<SocialUserAuthVO> socialUserAuthVOR = ISystemClient.authSocialUser(authSocialLoginDTO);
        UserInfoVO userInfoVO = null;
        if(socialUserAuthVOR.isSuccess()){
            SocialUserAuthVO socialUserAuthVO = socialUserAuthVOR.getData();
            if(socialUserAuthVO!=null){
                R<UserInfoVO> userInfoVOR = ISystemClient.userInfo(socialUserAuthVO.getUserId());
                if(userInfoVOR.isSuccess()){
                    userInfoVO = userInfoVOR.getData();
                }
            }
        }
        if(userInfoVO == null ){
            throw new UsernameNotFoundException(MRKI18N.USER_NOT_FOUND.getMessage());
        }
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        MrkUserDetails mrkUserDetails = new MrkUserDetails(userInfoVO, grantedAuthorities);
        // 组装认证数据，关闭密码校验
        Authentication userAuth = new UsernamePasswordAuthenticationToken(mrkUserDetails, null, mrkUserDetails.getAuthorities());
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        // 返回 OAuth2Authentication
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
