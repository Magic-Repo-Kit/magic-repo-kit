package com.magicrepokit.auth.config;

import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.auth.granter.MRKTokenGranter;
import com.magicrepokit.auth.service.MRKClientDetailsServiceImpl;
import com.magicrepokit.auth.support.MRKJwtTokenEnhancer;
import com.magicrepokit.jwt.properties.JWTProperties;
import com.magicrepokit.system.feign.SystemClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 认证服务器配置
 */
@Configuration
@EnableAuthorizationServer //开启资源认证服务
public class MRKAuthorizationService extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private  TokenStore tokenStore;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtAccessTokenConverter  accessTokenConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;
    @Autowired
    private TokenEnhancer tokenEnhancer;
    @Autowired
    private JWTProperties jwtProperties;
    @Autowired
    private SystemClient systemClient;
    @Autowired
    private MRKOAuthRequestFactory mrkoAuthRequestFactory;
    /**
     * 配置客户端详情信息
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        JdbcClientDetailsService clientDetailsService = new MRKClientDetailsServiceImpl(dataSource);
        //从数据库获取客户端信息
        clientDetailsService.setSelectClientDetailsSql(MRKAuthConstant.SELECT_BY_CLIENT_ID);
        clientDetailsService.setFindClientDetailsSql(MRKAuthConstant.BASE_SELECT);
        //设置密码验证方法
        clientDetailsService.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(clientDetailsService);
    }

    /**
     * 令牌访问端点配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //获取自定义tokenGranter
        TokenGranter tokenGranter = MRKTokenGranter.getTokenGranter(authenticationManager, endpoints, systemClient);
        //令牌增强器
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancerList = new ArrayList<>();
        enhancerList.add(tokenEnhancer);
        enhancerList.add(accessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(enhancerList);

        endpoints.authenticationManager(authenticationManager) //密码授权服务
                .authorizationCodeServices(authorizationCodeServices) //授权码服务
                .userDetailsService(userDetailsService)
                .tokenGranter(tokenGranter) //自定义服务
                .tokenEnhancer(tokenEnhancerChain)
                .accessTokenConverter(accessTokenConverter)
                .requestFactory(mrkoAuthRequestFactory)
//                .tokenServices(tokenServices()) //token管理服务
                .allowedTokenEndpointRequestMethods(HttpMethod.POST)//允许post请求
        ;
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
        services.setAccessTokenValiditySeconds(jwtProperties.getAccessTokenValiditySeconds());   //2小时
        services.setRefreshTokenValiditySeconds(jwtProperties.getRefreshTokenValiditySeconds()); //7天
        return services;
    }
}
