package com.magicrepokit.auth.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 认证服务器配置
 */
@Configuration
@EnableAuthorizationServer //开启资源认证服务
public class MRKAuthorizationService extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private  TokenStore tokenStore;
    @Autowired
    private  ClientDetailsService clientDetailsService;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private  AuthorizationCodeServices authorizationCodeServices;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAccessTokenConverter  accessTokenConverter;

    /**
     * 配置客户端详情信息
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()//使用内存存储
                .withClient("c1")
                .secret(new BCryptPasswordEncoder().encode("secret"))
                .resourceIds("res1")
                .authorizedGrantTypes("authorization_code",
                        "password","client_credentials","implicit","refresh_token") //申请资源token方式
                .scopes("all") //允许的授权范围
                .autoApprove(false) //是否跳转授权页面
                .redirectUris("http://www.baidu.com");

    }

    /**
     * 令牌访问端点配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager) //密码授权服务
                .authorizationCodeServices(authorizationCodeServices) //授权码服务
                .tokenServices(tokenServices()) //token管理服务
                .userDetailsService(userDetailsService)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST); //允许post请求
    }

    /**
     * 安全策略
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .allowFormAuthenticationForClients() //允许表认证
                .tokenKeyAccess("permitAll()") //oauth/token_key公开
                .checkTokenAccess("permitAll()"); //oauth/check_token公开
    }


    /**
     * 令牌服务
     * @return
     */
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services = new DefaultTokenServices();
        services.setClientDetailsService(clientDetailsService); //客户端信息
        services.setSupportRefreshToken(true); //是否刷新令牌
        services.setTokenStore(tokenStore); //令牌存储方式
        services.setAccessTokenValiditySeconds(7200); //2小时
        services.setRefreshTokenValiditySeconds(3600*24*7); //7天
        //令牌增强器
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        services.setTokenEnhancer(tokenEnhancerChain);
        return services;
    }

    /**
     * 配置授权码服务
     * @return
     */
    @Bean
    public AuthorizationCodeServices codeServices(){
        return  new InMemoryAuthorizationCodeServices();
    }
}
