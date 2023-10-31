package com.magicrepokit.system.service.impl;



import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.common.utils.WebUtil;
import com.magicrepokit.jwt.constant.JWTConstant;
import com.magicrepokit.jwt.utils.JWTUtil;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.system.constant.SocialTypeEnum;
import com.magicrepokit.social.factory.MRKAuthRequestFactory;
import com.magicrepokit.system.constant.SystemConstant;
import com.magicrepokit.system.constant.SystemResultCode;
import com.magicrepokit.system.constant.SystemUserStatus;
import com.magicrepokit.system.entity.User;
import com.magicrepokit.system.entity.dto.AuthLoginDTO;
import com.magicrepokit.system.entity.dto.AuthSocialLoginDTO;
import com.magicrepokit.system.entity.vo.AuthTokenVO;
import com.magicrepokit.system.entity.vo.SocialUserAuthVO;
import com.magicrepokit.system.service.IAuthService;
import com.magicrepokit.system.service.ISocialUserService;
import com.magicrepokit.system.service.IUserService;
import com.magicrepokit.system.entity.vo.UserInfoVO;
import com.xingyuv.jushauth.request.AuthRequest;
import com.xingyuv.jushauth.utils.AuthStateUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 登录授权服务
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private IUserService userService;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private MRKAuthRequestFactory mrkAuthRequestFactory;
    @Autowired
    private ISocialUserService socialUserService;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${mrk.auth.local.client-id}")
    private String clientId;
    @Value("${mrk.auth.local.client-secret}")
    private String clientSecret;

    /**
     *  登录
     *
     * @param authLoginDTO 登录信息
     * @return 登录信息
     */
    @Override
    public AuthTokenVO login(AuthLoginDTO authLoginDTO) {
        //校验用户
        UserInfoVO authenticate = authenticate(authLoginDTO.getUsername(), authLoginDTO.getPassword());
        //oauth登录获取令牌

        return remoteTokenService(JWTConstant.PASSWORD, clientId, clientSecret, authLoginDTO.getUsername(), authLoginDTO.getPassword(), null,null,null,null);
    }

    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 令牌信息
     */
    @Override
    public AuthTokenVO refreshToken(String refreshToken) {
        //解析token
        Claims claims = JWTUtil.parseJWT(refreshToken);
        if(claims==null){
            throw new ServiceException(SystemResultCode.REFRESH_TOKEN_FAIL);
        }
        String userId = String.valueOf(claims.get(JWTConstant.USER_ID));
        String refreshTokenRedis = JWTUtil.getRefreshToken(Long.valueOf(userId), getUserType());
        if(refreshTokenRedis==null||!refreshTokenRedis.equals(refreshToken)){
            throw new ServiceException(SystemResultCode.REFRESH_TOKEN_FAIL);
        }
        return remoteTokenService(JWTConstant.REFRESH_TOKEN,clientId,clientSecret,null,null,refreshToken,null,null,null);
    }

    /**
     *  获取三方认证请求地址
     *
     * @param type 三方平台 10：github 20:google 30:gitee
     * @param redirectUri 本系统回掉地址
     * @return 三方请求地址
     */
    @Override
    public String socialLoginRedirect(Integer type, String redirectUri) {
        SocialTypeEnum socialTypeEnum = SocialTypeEnum.valueOfType(type);
        if(socialTypeEnum==null){
            throw new ServiceException(SystemResultCode.NOT_FOUND_SOCIAL_TYPE);
        }
        // 获得对应的 AuthRequest 实现
        AuthRequest authRequest = mrkAuthRequestFactory.get(socialTypeEnum.getSource());
        // 生成跳转地址
        String authorizeUri = authRequest.authorize(AuthStateUtils.createState());
        return WebUtil.replaceUrlQuery(authorizeUri, "redirect_uri", redirectUri);
    }

    /**
     * 三方快捷登录换取本系统token
     *
     * @param authSocialLoginDTO 三方code相关信息
     * @return 令牌信息
     */
    @Override
    public AuthTokenVO socialLogin(AuthSocialLoginDTO authSocialLoginDTO) {
        //1.获取社交用户信息
        socialUserService.authSocialUser(authSocialLoginDTO.getType(), authSocialLoginDTO.getCode(), authSocialLoginDTO.getState());
        //2.获取系统用户信息
        return remoteTokenService(JWTConstant.SOCIAL, clientId, clientSecret, null, null, null,authSocialLoginDTO.getType(),authSocialLoginDTO.getCode(),authSocialLoginDTO.getState());
    }


    /**
     * 认证中心获取令牌
     *
     * @param grantType 授权类型
     * @param clientId 客户端id
     * @param clientSecret 客户端密码
     * @param username 用户名
     * @param password 密码
     * @param refreshToken 刷新token
     * @return 令牌信息
     */
    private AuthTokenVO remoteTokenService(String grantType,String clientId,String clientSecret,String username,String password,String refreshToken,Integer socialType,String socialCode,String socialState){
        String userType = getUserType();
        //负载获取远程服务
        ServiceInstance serviceInstance = loadBalancerClient.choose(SystemConstant.REMOTE_AUTH_NAME);
        if(serviceInstance==null){
            throw new ServiceException(SystemResultCode.NOT_FOUND_SERVICE,SystemConstant.REMOTE_AUTH_NAME);
        }
        //获取远程地址
        String path = serviceInstance.getUri().toString()+SystemConstant.OAUTH_TOKEN_URL;
        //定义body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(JWTConstant.GRANT_TYPE,grantType);
        formData.add(JWTConstant.USERNAME,username);
        formData.add(JWTConstant.PASSWORD,password);
        formData.add(JWTConstant.REFRESH_TOKEN,refreshToken);
        formData.add(JWTConstant.SOURCE,socialType==null?null:socialType+"");
        formData.add(JWTConstant.CODE,socialCode);
        formData.add(JWTConstant.STATE,socialState);
        String queryParams = WebUtil.buildQueryParams(formData);
        String urlWithParams = path + queryParams;

        //定义请求头
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add(JWTConstant.AUTHORIZATION, JWTUtil.getAuthorization(clientId,clientSecret));
        header.add(JWTConstant.USER_TYPE,userType);
        //远程请求
        Map<String,Object> remoteResult;
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(urlWithParams, HttpMethod.POST, new HttpEntity<>(null, header), Map.class);
            remoteResult = responseEntity.getBody();
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ServiceException(SystemResultCode.REMOTE_SERVICE_ERROR,SystemConstant.REMOTE_AUTH_NAME);
        }
        if(remoteResult == null || remoteResult.get(JWTConstant.ACCESS_TOKEN) == null || remoteResult.get(JWTConstant.REFRESH_TOKEN) == null || remoteResult.get(JWTConstant.JTI) == null) {
            //jti是jwt令牌的唯一标识作为用户身份令牌
            throw new ServiceException(SystemResultCode.CREATE_JWT_FAILED);
        }
        return BeanUtil.toBean(remoteResult, AuthTokenVO.class);
    }

    /**
     * 请求头获取用户类别
     *
     * @return userType
     */
    private static String getUserType() {
        HttpServletRequest request = WebUtil.getRequest();
        String userType = request.getHeader(JWTConstant.USER_TYPE);
        if(userType==null){
            throw new ServiceException(SystemResultCode.NOT_FOUND_USER_TYPE);
        }
        return userType;
    }


    /**
     * 验证账户密码
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public UserInfoVO authenticate(String username, String password){
        //查询用户信息
        UserInfoVO userInfoVO = userService.userInfo(username);
        if(ObjectUtil.isEmpty(userInfoVO)){
            throw new ServiceException(SystemResultCode.NOT_FOUND_USER);
        }
        //校验密码
        if (!userService.isPasswordMatch(password, userInfoVO.getUser().getPassword())) {
            throw new ServiceException(SystemResultCode.NOT_FOUND_USER);
        }
        //判断是否激活
        if(userInfoVO.getUser().getStatus()== SystemUserStatus.Disabled.getCode()){
            throw new ServiceException(SystemResultCode.DISABLED_USER);
        }
        return userInfoVO;
    }
}
