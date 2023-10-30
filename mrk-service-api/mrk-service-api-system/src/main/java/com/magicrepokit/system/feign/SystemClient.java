package com.magicrepokit.system.feign;

import com.magicrepokit.common.api.R;
import com.magicrepokit.system.constant.SystemConstant;
import com.magicrepokit.system.entity.dto.AuthSocialLoginDTO;
import com.magicrepokit.system.entity.vo.SocialUserAuthVO;
import com.magicrepokit.system.entity.vo.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = SystemConstant.APPLICATION_USER_NAME)
public interface SystemClient {
    String USER_INFO = "user/info";

    String USER_INFO_ID = "user/info/{id}";

    String Auth_Social = "social//auth-social-user";

    /**
     * 用户信息
     *
     * @param account
     * @return
     */
    @GetMapping(USER_INFO)
    R<UserInfoVO> userInfo(@RequestParam("account") String account);

    @GetMapping(USER_INFO_ID)
    R<UserInfoVO> userInfo(@PathVariable("id") Long id);

    /**
     * 三方登录认证
     * @param authSocialLoginDTO
     * @return
     */
    @PostMapping(Auth_Social)
    R<SocialUserAuthVO> authSocialUser(@RequestBody AuthSocialLoginDTO authSocialLoginDTO);
}