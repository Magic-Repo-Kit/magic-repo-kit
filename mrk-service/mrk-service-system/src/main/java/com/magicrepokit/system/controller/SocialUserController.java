package com.magicrepokit.system.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.system.entity.dto.AuthSocialLoginDTO;
import com.magicrepokit.system.entity.vo.SocialUserAuthVO;
import com.magicrepokit.system.service.ISocialUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("social")
@AllArgsConstructor
public class SocialUserController {
    private ISocialUserService socialUserService;
}
