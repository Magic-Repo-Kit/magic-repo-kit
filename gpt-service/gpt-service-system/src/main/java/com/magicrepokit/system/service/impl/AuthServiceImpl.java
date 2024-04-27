package com.magicrepokit.system.service.impl;




import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.common.utils.GPTUtil;
import com.magicrepokit.common.utils.WebUtil;
import com.magicrepokit.jwt.constant.JWTConstant;
import com.magicrepokit.jwt.utils.JWTUtil;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.redis.utils.GPTRedisUtils;
import com.magicrepokit.system.config.mail.MailUtil;
import com.magicrepokit.system.constant.SocialTypeEnum;
import com.magicrepokit.system.constant.SystemConstant;
import com.magicrepokit.system.constant.SystemResultCode;
import com.magicrepokit.system.constant.SystemUserStatus;
import com.magicrepokit.system.dto.auth.AuthLoginDTO;
import com.magicrepokit.system.dto.auth.AuthSocialLoginDTO;
import com.magicrepokit.system.dto.auth.UserForgetPassword;
import com.magicrepokit.system.dto.auth.UserRegister;
import com.magicrepokit.system.vo.auth.AuthTokenVO;
import com.magicrepokit.system.service.IAuthService;
import com.magicrepokit.system.service.ISocialUserService;
import com.magicrepokit.system.service.IUserService;
import com.magicrepokit.system.vo.user.UserInfoVO;
import com.xingyuv.jushauth.request.AuthRequest;
import com.xingyuv.jushauth.utils.AuthStateUtils;
import com.xingyuv.justauth.AuthRequestFactory;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.thymeleaf.context.Context;


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
@AllArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final IUserService userService;
    private final LoadBalancerClient loadBalancerClient;
    private final AuthRequestFactory authRequestFactory;
    private final ISocialUserService socialUserService;
    private final RestTemplate restTemplate;
    private final GPTRedisUtils redisUtils;
    @Value("${mrk.auth.local.client-id}")
    private final String clientId = null;
    @Value("${mrk.auth.local.client-secret}")
    private final String clientSecret = null;
    private final static String REDIS_REGISTER_KEY = "register:";
    private final static String REDIS_FORGET_KEY = "forget:";
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
        return remoteTokenService(JWTConstant.PASSWORD, clientId, clientSecret, authenticate.getUser().getAccount(), authLoginDTO.getPassword(), null,null,null,null);
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
        AuthRequest authRequest = authRequestFactory.get(socialTypeEnum.getSource());
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
     * 用户注册
     * @param userRegister 注册信息
     * @return 注册结果
     */
    @Override
    public Boolean register(UserRegister userRegister) {
        //0.账户是否已经注册
        if(ObjectUtil.isEmpty(userRegister.getAccount())||userService.checkAccount(userRegister.getAccount())){
            throw new ServiceException(SystemResultCode.USERNAME_EXIST);
        }
        //1.判断邮箱是否注册
        if(ObjectUtil.isEmpty(userRegister.getEmail())||!MailUtil.checkEmail(userRegister.getEmail())||userService.checkEmail(userRegister.getEmail())){
            throw new ServiceException(SystemResultCode.EMAIL_ERROR);
        }
        //2.判断验证码是否正确
        if(!redisUtils.hasKey(REDIS_REGISTER_KEY + userRegister.getEmail())||!redisUtils.get(REDIS_REGISTER_KEY + userRegister.getEmail()).equals(userRegister.getVerificationCode())){
            throw new ServiceException(SystemResultCode.CAPTCHA_ERROR);
        }
        //3.密码校验
        if(ObjectUtil.isEmpty(userRegister.getPassword())||ObjectUtil.isEmpty(userRegister.getConfirmPassword())||!userRegister.getPassword().equals(userRegister.getConfirmPassword())){
            throw new ServiceException(SystemResultCode.PASSWORD_NOT_EQUAL);
        }
        //4.注册
        boolean flag =  userService.register(userRegister);
        //5.删除验证码
        redisUtils.del(REDIS_REGISTER_KEY + userRegister.getEmail());
        return flag;
    }

    /**
     * 发送验证码
     * @param type 1:注册 2:忘记密码
     * @param email 邮箱
     * @return 是否发送成功
     */
    @Override
    public Boolean sendCode(Integer type, String email) {
        if(ObjectUtil.isEmpty(type)||ObjectUtil.isEmpty(email)){
            return false;
        }
        //判断邮箱是否正确
        if(!MailUtil.checkEmail(email)){
            throw new ServiceException(SystemResultCode.EMAIL_FORMAT_ERROR);
        }
        //判断验证码是否存在
        if(redisUtils.hasKey(REDIS_REGISTER_KEY + email)||redisUtils.hasKey(REDIS_FORGET_KEY + email)){
            throw new ServiceException(SystemResultCode.EMAIL_SEND);
        }
        //1.生成验证码
        String code = GPTUtil.emailVerificationCode();
        //2.存入redis
        switch (type) {
            case 1:
                redisUtils.setExpire(REDIS_REGISTER_KEY + email, code, 60);
                break;
            case 2:
                redisUtils.setExpire(REDIS_FORGET_KEY + email, code, 60);
                break;
            default:
                return false;
        }
        //3.发送邮件
        Context context = new Context();
        context.setVariable("verificationCode", code);
        MailUtil.sendHtml(email, "MagicRepoKit 认证消息", context, null);
        return true;
    }

    /**
     * 忘记密码
     * @param userForgetPassword 忘记密码信息
     * @return 是否修改成功
     */
    @Override
    public boolean forgetPassword(UserForgetPassword userForgetPassword) {
        //1.判断邮箱是否注册
        if(ObjectUtil.isEmpty(userForgetPassword.getEmail())||!MailUtil.checkEmail(userForgetPassword.getEmail())||!userService.checkEmail(userForgetPassword.getEmail())){
            throw new ServiceException(SystemResultCode.EMAIL_ERROR);
        }
        //2.判断验证码是否正确
        if(!redisUtils.hasKey(REDIS_FORGET_KEY + userForgetPassword.getEmail())||!redisUtils.get(REDIS_FORGET_KEY + userForgetPassword.getEmail()).equals(userForgetPassword.getVerificationCode())){
            throw new ServiceException(SystemResultCode.CAPTCHA_ERROR);
        }
        //3.密码校验
        if(ObjectUtil.isEmpty(userForgetPassword.getPassword())||ObjectUtil.isEmpty(userForgetPassword.getConfirmPassword())||!userForgetPassword.getPassword().equals(userForgetPassword.getConfirmPassword())){
            throw new ServiceException(SystemResultCode.PASSWORD_NOT_EQUAL);
        }
        //4.修改密码
        boolean flag = userService.forgetPassword(userForgetPassword);
        //5.删除验证码
        redisUtils.del(REDIS_FORGET_KEY + userForgetPassword.getEmail());
        return flag;
    }

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    @Override
    public boolean checkEmail(String email) {
        if(ObjectUtil.isEmpty(email)){
            return false;
        }
        return userService.checkEmail(email);
    }

    /**
     * 检查账户是否存在
     * @param account 账户
     * @return 是否存在
     */
    @Override
    public boolean checkAccount(String account) {
        if(ObjectUtil.isEmpty(account)){
            return false;
        }
        return userService.checkAccount(account);
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
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
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
        UserInfoVO userInfoVO;
        //检查是否邮箱
        if(MailUtil.checkEmail(username)){
            userInfoVO= userService.userInfoByEmail(username);
        }else{
            //根据用户名查询用户信息
            userInfoVO= userService.userInfo(username);
        }

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
