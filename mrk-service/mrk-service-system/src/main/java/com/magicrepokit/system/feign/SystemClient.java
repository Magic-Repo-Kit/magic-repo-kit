package com.magicrepokit.system.feign;

import com.magicrepokit.common.api.R;
import com.magicrepokit.system.dto.auth.AuthSocialLoginDTO;
import com.magicrepokit.system.vo.auth.SocialUserAuthVO;
import com.magicrepokit.system.vo.user.UserInfoVO;
import com.magicrepokit.system.service.ISocialUserService;
import com.magicrepokit.system.service.IUserService;
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
    @GetMapping(Auth_Social)
    public R<SocialUserAuthVO> authSocialUser(@RequestBody AuthSocialLoginDTO authSocialLoginDTO) {
        return R.data(socialUserService.authSocialUser(authSocialLoginDTO.getType(),authSocialLoginDTO.getCode(), authSocialLoginDTO.getState()));
    }
}
