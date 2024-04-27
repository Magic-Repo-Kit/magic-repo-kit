package com.gpt.system.feign;

import com.gpt.common.api.R;
import com.gpt.system.dto.auth.AuthSocialLoginDTO;
import com.gpt.system.vo.auth.SocialUserAuthVO;
import com.gpt.system.vo.user.UserInfoVO;
import com.gpt.system.service.ISocialUserService;
import com.gpt.system.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class SystemClient implements ISystemClient{
    private final IUserService userService;
    private final ISocialUserService socialUserService;

    /**
     * 获取用户信息
     * @param account
     * @return
     */
    @Override
    @GetMapping(USER_INFO)
    public R<UserInfoVO> userInfo(@RequestParam("account") String account) {
        return R.data(userService.userInfo(account));
    }

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    @Override
    @GetMapping(USER_INFO_ID)
    public R<UserInfoVO> userInfo(@PathVariable("id") Long id) {
        return R.data(userService.userInfo(id));
    }

    /**
     * 获取社交用户信息
     * @param authSocialLoginDTO
     * @return
     */
    @Override
    @PostMapping(Auth_Social)
    public R<SocialUserAuthVO> authSocialUser(AuthSocialLoginDTO authSocialLoginDTO) {
        return R.data(socialUserService.authSocialUser(authSocialLoginDTO.getType(),authSocialLoginDTO.getCode(), authSocialLoginDTO.getState()));
    }
}
