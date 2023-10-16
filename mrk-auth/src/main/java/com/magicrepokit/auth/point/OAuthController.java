package com.magicrepokit.auth.point;

import com.magicrepokit.common.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {
    @Autowired
    private TokenEndpoint tokenEndpoint;


    @GetMapping("/point/test")
    public R login(){
        System.out.println("测试");
        return R.success("success");
    }

}
