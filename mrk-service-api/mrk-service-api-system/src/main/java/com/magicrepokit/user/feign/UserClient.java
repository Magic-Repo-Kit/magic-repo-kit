package com.magicrepokit.user.feign;

import com.magicrepokit.common.api.R;
import com.magicrepokit.user.constant.AppConstant;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = AppConstant.APPLICATION_USER_NAME)
public interface UserClient {
    String USER_INFO = "user/info";

    @GetMapping(USER_INFO)
    R<UserInfo> userInfo(@RequestParam("account") String account, @RequestParam("userType") Integer userType);
}