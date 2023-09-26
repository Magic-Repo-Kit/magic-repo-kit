package com.magicrepokit.auth.point;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/point")
public class OAuthController {

    @PostMapping("/test1")
    public String test1(){
        return "这是一个测试接口1";
    }

    @PostMapping("/test2")
    public String test2(){
        return "这是一个测试接口1";
    }
}
