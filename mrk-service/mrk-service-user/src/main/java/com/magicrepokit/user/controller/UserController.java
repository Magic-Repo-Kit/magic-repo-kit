package com.magicrepokit.user.controller;

import com.magicrepokit.common.api.R;
import com.magicrepokit.common.api.ResultCode;
import com.magicrepokit.i18n.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping
public class UserController {
    @Value("classpath:i18n/")
    private Resource resource;
    @GetMapping("/version")
    public R version(){
        return R.data(ResultCode.SUCCESS.getMessage(),MessageUtil.getMessage("VERSION"));
    }
}
