package com.magicrepokit.auth.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.magicrepokit.auth.constant.MRKAuthGrantTypeEnum;
import com.magicrepokit.auth.constant.MRKI18N;
import com.magicrepokit.auth.entity.vo.AuthAccessTokenVO;
import com.magicrepokit.common.api.R;
import com.magicrepokit.common.exception.ServiceException;
import com.magicrepokit.common.utils.WebUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证中心开放接口
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("/auth")
public class MRKAuthController {

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
    public R<AuthAccessTokenVO> postAccessToken(@RequestParam("grant_type") String grantType,
                                                @RequestParam(value = "code", required = false) String code, // 授权码模式
                                                @RequestParam(value = "redirect_uri", required = false) String redirectUri, // 授权码模式
                                                @RequestParam(value = "state", required = false) String state, // 授权码模式
                                                @RequestParam(value = "username", required = false) String username, // 密码模式
                                                @RequestParam(value = "password", required = false) String password, // 密码模式
                                                @RequestParam(value = "scope", required = false) String scope, // 密码模式
                                                @RequestParam(value = "refresh_token", required = false) String refreshToken){
        //检验授权模式
        MRKAuthGrantTypeEnum grantTypeEnum = MRKAuthGrantTypeEnum.getByGranType(grantType);
        if(ObjectUtil.isEmpty(grantTypeEnum)){
            throw new ServiceException(StrUtil.format(MRKI18N.UNKNOWN_GRANT_TYPE.getMessage(),grantType));
        }
        //获取客户id和密钥
        String[] clientIdAndSecret = basicAuthorization();
        //校验客户端

        return null;
    }

    /**
     * 获取客户端和密钥
     * @return
     */
    private String[] basicAuthorization() {
        String[] clientIdAndSecret = WebUtil.BasicAuthorization();
        if(ObjectUtil.isEmpty(clientIdAndSecret)){
            throw new ServiceException(MRKI18N.UNKNOWN_GRANT_TYPE.getMessage());
        }
        return clientIdAndSecret;
    }
}
