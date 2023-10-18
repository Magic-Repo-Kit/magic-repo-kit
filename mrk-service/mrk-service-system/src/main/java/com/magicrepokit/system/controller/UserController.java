package com.magicrepokit.system.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.i18n.utils.MessageUtil;
import com.magicrepokit.system.service.IUserService;
import com.magicrepokit.system.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 用户服务
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private IUserService userService;

    /**
     * i18n测试
     * @return
     */
    @GetMapping("/version")
    public R<String> version(){
        return R.data(MessageUtil.getMessage("VERSION"));
    }

    /**
     * 用户信息
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    public R<UserInfo> userInfo(@PathVariable Long id){
        return R.data(userService.userInfo(id));
    }

    /**
     * 用户信息
     * @param account
     * @return
     */
    @GetMapping("/info")
    public R<UserInfo> userInfo(@RequestParam("account") String account){
        return R.data(userService.userInfo(account));
    }
}
