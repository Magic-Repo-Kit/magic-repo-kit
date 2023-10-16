package com.magicrepokit.user.feign;

import com.magicrepokit.common.api.R;
import com.magicrepokit.user.constant.AppConstant;
import com.magicrepokit.user.entity.AuthClient;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@FeignClient(value = AppConstant.APPLICATION_USER_NAME)
public interface SystemClient {
    String USER_INFO = "user/info";

    String VALID_CLIENT = "oauth2-client/valid-oauth-client";

    String INFO_CLIENT = "oauth2-client/info-oauth-client";

    /**
     * 用户信息
     *
     * @param account
     * @return
     */
    @GetMapping(USER_INFO)
    R<UserInfo> userInfo(@RequestParam("account") String account);


    /**
     * 校验客户端信息
     * @param clientId
     * @param clientSecret
     * @param authorizedGrantType
     * @param scopes
     * @param redirectUri
     * @return
     */
    @PostMapping(VALID_CLIENT)
    R<AuthClient> validOAuthClientFromCache(@RequestParam("clientId") String clientId,
                                            @RequestParam("clientSecret") String clientSecret,
                                            @RequestParam(value = "grantType", required = false) String authorizedGrantType,
                                            @RequestParam(value = "scopes", required = false) Collection<String> scopes,
                                            @RequestParam(value = "redirectUri", required = false) String redirectUri);

    /**
     * 获取客户端信息
     *
     * @param clientId
     * @return
     */
    @PostMapping(INFO_CLIENT)
    R<AuthClient> authClientInfo(@RequestParam("clientId")String clientId);
}