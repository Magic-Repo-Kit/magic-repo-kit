package com.magicrepokit.system.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.system.entity.dto.LoginDTO;
import com.magicrepokit.system.entity.vo.AuthTokenVO;
import com.magicrepokit.system.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * 登录认证接口
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private IAuthService authService;


    /**
     * 用户登录接口
     *
     * @param loginDTO 登录实体类
     * @return 令牌信息
     */
    @PostMapping("/login")
    public R<AuthTokenVO> Login(@RequestBody @Valid LoginDTO loginDTO){
        return R.data(authService.login(loginDTO));
    }
}

