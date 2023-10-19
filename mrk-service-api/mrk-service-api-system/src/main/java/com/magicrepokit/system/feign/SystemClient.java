package com.magicrepokit.system.feign;

import com.magicrepokit.common.api.R;
import com.magicrepokit.system.constant.SystemConstant;
import com.magicrepokit.system.vo.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = SystemConstant.APPLICATION_USER_NAME)
public interface SystemClient {
    String USER_INFO = "user/info";

    /**
     * 用户信息
     *
     * @param account
     * @return
     */
    @GetMapping(USER_INFO)
    R<UserInfo> userInfo(@RequestParam("account") String account);
}