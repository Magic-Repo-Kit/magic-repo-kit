package com.magicrepokit.system.controller;

import com.magicrepokit.system.service.IUserService;
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
