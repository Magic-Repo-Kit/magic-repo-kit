package com.magicrepokit.system.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.i18n.utils.MessageUtil;
import com.magicrepokit.system.service.IUserService;
import com.magicrepokit.system.entity.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 用户服务
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;


}
