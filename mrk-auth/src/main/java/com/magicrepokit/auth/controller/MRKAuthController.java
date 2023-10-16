package com.magicrepokit.auth.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.magicrepokit.auth.constant.MRKAuthGrantTypeEnum;
import com.magicrepokit.auth.constant.MRKI18N;
import com.magicrepokit.auth.entity.vo.AuthAccessTokenVO;
import com.magicrepokit.auth.service.MRKAuthGrantService;
import com.magicrepokit.auth.utils.AuthUtils;
import com.magicrepokit.common.api.R;
import com.magicrepokit.common.api.ResultCode;
import com.magicrepokit.common.utils.WebUtil;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.user.entity.AuthClient;
import com.magicrepokit.user.feign.SystemClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 认证中心开放接口
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("/mrk-auth")
public class MRKAuthController {
    @Autowired
    private SystemClient systemClient;
    @Autowired
    private MRKAuthGrantService mrkAuthGrantService;

    /**
     * 获取token
     *
     * @param grantType 授权类型: 必填 [code,password,refresh_token]
     * @param code 授权码: 只有授权码类型填入
     * @param redirectUri 重定向地址
     * @param state 状态
     * @param username 用户名: 只有密码模式填入
     * @param password 密码：只有密码模式填入
     * @param scope 授权范围
     * @param refreshToken 刷新token: 只有刷新token模式下填入
     * @return
     */
    @PostMapping("/token")
    public R<AuthAccessTokenVO> postAccessToken(@RequestParam("grantType") String grantType,
                                                @RequestParam(value = "code", required = false) String code, // 授权码模式
                                                @RequestParam(value = "redirectUri", required = false) String redirectUri, // 重定向Uri
                                                @RequestParam(value = "state", required = false) String state, // 状态
                                                @RequestParam(value = "username", required = false) String username, // 用户名
                                                @RequestParam(value = "password", required = false) String password, // 密码
                                                @RequestParam(value = "scope", required = false) String scope, // 授权范围
                                                @RequestParam(value = "refresh_token", required = false) String refreshToken ){ //刷新token
        List<String> scopes = AuthUtils.buildScopes(scope);
        //检验授权模式
        MRKAuthGrantTypeEnum grantTypeEnum = MRKAuthGrantTypeEnum.getByGranType(grantType);
        if(ObjectUtil.isEmpty(grantTypeEnum)){
            throw new ServiceException(ResultCode.FAILURE,StrUtil.format(MRKI18N.UNKNOWN_GRANT_TYPE.getMessage(), grantType));
        }
        //获取客户id和密钥
        String[] clientIdAndSecret = basicAuthorization();
        //校验客户端
        AuthClient client = mrkAuthGrantService.validOAuthClient(clientIdAndSecret[0], clientIdAndSecret[1],
                grantType, scopes, redirectUri);

        AuthAccessTokenVO accessTokenVO;
        //根据类型获取token
        switch (grantTypeEnum){
            //密码模式
            case PASSWORD:
                accessTokenVO = mrkAuthGrantService.grantPassword(username,password,client.getClientId(),scopes);
                break;
            //授权码模式
            case AUTHORIZATION_CODE:
                accessTokenVO = mrkAuthGrantService.grantAuthorizationCodeForAccessToken(client.getClientId(), code, redirectUri, state);
                break;
            //刷新token
            case REFRESH_TOKEN:
                accessTokenVO = mrkAuthGrantService.grantRefreshToken(refreshToken, client.getClientId());
                break;
            default:
                throw new ServiceException(ResultCode.FAILURE,StrUtil.format(MRKI18N.UNKNOWN_GRANT_TYPE.getMessage(), grantType));
        }
        Assert.notNull(accessTokenVO, MRKI18N.NOT_FOUND_CLIENT.getMessage());

        return R.data(accessTokenVO);
    }

    /**
     * 获取客户端和密钥
     * @return
     */
    private String[] basicAuthorization() {
        String[] clientIdAndSecret = WebUtil.BasicAuthorization();
        if(ObjectUtil.isEmpty(clientIdAndSecret)){
            throw new ServiceException(ResultCode.FAILURE,MRKI18N.NOT_FOUND_AUTHORIZATION.getMessage());
        }
        return clientIdAndSecret;
    }
}
