package com.magicrepokit.user.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.common.api.ResultCode;
import com.magicrepokit.common.utils.MessageUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
    @GetMapping("/version")
    public R version(){
        return R.success(ResultCode.SUCCESS);
    }
}
