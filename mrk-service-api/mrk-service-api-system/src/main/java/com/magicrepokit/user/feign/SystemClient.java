package com.magicrepokit.user.feign;

import com.magicrepokit.common.api.R;
import com.magicrepokit.user.constant.AppConstant;
import com.magicrepokit.user.entity.OAuth2Client;
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

    @GetMapping(USER_INFO)
    R<UserInfo> userInfo(@RequestParam("account") String account, @RequestParam("userType") Integer userType);



    @PostMapping(VALID_CLIENT)
    R<OAuth2Client> validOAuthClientFromCache(@RequestParam("clientId") String clientId,
                                              @RequestParam("clientSecret") String clientSecret,
                                              @RequestParam(value = "grantType", required = false) String authorizedGrantType,
                                              @RequestParam(value = "scopes", required = false) Collection<String> scopes,
                                              @RequestParam(value = "redirectUri", required = false) String redirectUri);
}