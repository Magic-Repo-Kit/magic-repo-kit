package com.magicrepokit.client;

import cn.hutool.crypto.digest.BCrypt;
import com.magicrepokit.user.MRKSystemApplication;
import com.magicrepokit.user.entity.AuthClient;
import com.magicrepokit.user.service.IOAuth2ClientService;
import com.magicrepokit.user.service.impl.OAuth2ClientImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = MRKSystemApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan("com.magicrepokit")
public class ClientTest {
    @Autowired
    private IOAuth2ClientService ioAuth2ClientService;

    @Test
    public void setClient(){
        AuthClient client = new AuthClient();
        client.setClientId("test")
                .setSecret(BCrypt.hashpw("test"))
                .setName("测试")
                .setLogo("")
                .setDescription("测试应用")
                .setStatus(0)
                .setAccessTokenValiditySeconds(30*60)
                .setRefreshTokenValiditySeconds(3600*24*7)
                .setRedirectUris(Collections.singletonList("http://localhost:8080/test"))
                .setAuthorizedGrantTypes(Arrays.asList("password","code","refreshToken"))
                .setScopes(Arrays.asList("user:read","user:write"))
                .setAuthorities(Arrays.asList("user:read","user:write"))
                .setResourceIds(Arrays.asList("1","2"));
        ioAuth2ClientService.save(client);
    }
}
