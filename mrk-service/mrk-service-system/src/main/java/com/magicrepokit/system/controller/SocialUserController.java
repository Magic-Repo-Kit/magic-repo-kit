package com.magicrepokit.system.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.system.entity.dto.AuthSocialLoginDTO;
import com.magicrepokit.system.entity.vo.SocialUserAuthVO;
import com.magicrepokit.system.service.ISocialUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("social")
public class SocialUserController {
    @Autowired
    private ISocialUserService socialUserService;


    @PostMapping("/auth-social-user")
    public R<SocialUserAuthVO> authSocialUser(@RequestBody AuthSocialLoginDTO authSocialLoginDTO){
        return R.data(socialUserService.authSocialUser(authSocialLoginDTO.getType(),authSocialLoginDTO.getCode(), authSocialLoginDTO.getState()));
    }
}
