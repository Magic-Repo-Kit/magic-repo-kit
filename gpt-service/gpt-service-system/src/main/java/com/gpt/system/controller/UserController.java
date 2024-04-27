package com.gpt.system.controller;

import com.gpt.system.service.IUserService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class UserController {
    private IUserService userService;



}
