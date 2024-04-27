package com.gpt.system.feign;

import com.gpt.common.api.R;
import com.gpt.system.constant.SystemConstant;
import com.gpt.system.dto.auth.AuthSocialLoginDTO;
import com.gpt.system.vo.auth.SocialUserAuthVO;
import com.gpt.system.vo.user.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = SystemConstant.APPLICATION_NAME)
public interface ISystemClient {
    String API_PREFIX = "/system";
    String USER_INFO = API_PREFIX + "user/info";

    String USER_INFO_ID = API_PREFIX + "user/info/{id}";

    String Auth_Social = API_PREFIX + "social/auth-social-user";

    /**
     * 用户信息
     *
     * @param account
     * @return
     */
    @GetMapping(USER_INFO)
    R<UserInfoVO> userInfo(@RequestParam("account") String account);

    /**
     * 用户信息
     *
     * @param id
     * @return
     */
    @GetMapping(USER_INFO_ID)
    R<UserInfoVO> userInfo(@PathVariable("id") Long id);

    /**
     * 三方登录认证
     *
     * @param authSocialLoginDTO
     * @return
     */
    @PostMapping(Auth_Social)
    R<SocialUserAuthVO> authSocialUser(@RequestBody AuthSocialLoginDTO authSocialLoginDTO);
}