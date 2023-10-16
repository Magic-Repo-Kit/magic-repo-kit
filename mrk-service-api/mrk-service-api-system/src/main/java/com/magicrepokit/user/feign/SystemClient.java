package com.magicrepokit.user.feign;

import com.magicrepokit.common.api.R;
import com.magicrepokit.user.constant.AppConstant;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}