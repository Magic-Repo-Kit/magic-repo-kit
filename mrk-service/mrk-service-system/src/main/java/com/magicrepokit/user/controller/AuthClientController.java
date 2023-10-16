package com.magicrepokit.user.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.user.entity.AuthClient;
import com.magicrepokit.user.service.IOAuth2ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * oauth2的客户端服务
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("oauth2-client")
public class AuthClientController {
    @Autowired
    private IOAuth2ClientService auth2ClientService;

    /**
     * 从缓存中，校验客户端是否合法
     *
     * @param clientId            客户端编号
     * @param clientSecret        客户端密钥
     * @param authorizedGrantType 授权方式
     * @param scopes              授权范围
     * @param redirectUri         重定向地址
     * @return 客户端
     */
    @PostMapping("/valid-oauth-client")
    public R<AuthClient> validOAuthClientFromCache(@RequestParam("clientId") String clientId,
                                                   @RequestParam("clientSecret") String clientSecret,
                                                   @RequestParam(value = "grantType", required = false) String authorizedGrantType,
                                                   @RequestParam(value = "scopes", required = false) Collection<String> scopes,
                                                   @RequestParam(value = "redirectUri", required = false) String redirectUri) {
        return R.data(auth2ClientService.validOAuthClientFromCache(clientId, clientSecret, authorizedGrantType, scopes, redirectUri));
    }

    /**
     * 获取客户端信息
     *
     * @param clientId
     * @return
     */
    @PostMapping("/info-oauth-client")
    public R<AuthClient> authClientInfo(@RequestParam("clientId")String clientId){
        return R.data(auth2ClientService.infoClient(clientId));
    }
}
