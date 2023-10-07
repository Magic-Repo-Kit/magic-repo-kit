package com.magicrepokit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
    @Autowired
    private MessageSource messageSource;
    @GetMapping("/version")
    public String version(){
        return messageSource.getMessage("VERSION",null, LocaleContextHolder.getLocale());
    }
}
