package com.gpt.system.controller;

import com.gpt.system.service.ISocialUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("social")
@AllArgsConstructor
public class SocialUserController {
    private ISocialUserService socialUserService;
}
