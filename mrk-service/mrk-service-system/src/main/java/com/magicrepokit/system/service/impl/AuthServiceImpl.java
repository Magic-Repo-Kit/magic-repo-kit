package com.magicrepokit.system.service.impl;



import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.common.utils.WebUtil;
import com.magicrepokit.jwt.constant.JWTConstant;
import com.magicrepokit.jwt.utils.JWTUtil;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.system.constant.SystemConstant;
import com.magicrepokit.system.constant.SystemResultCode;
import com.magicrepokit.system.constant.SystemUserStatus;
import com.magicrepokit.system.entity.dto.LoginDTO;
import com.magicrepokit.system.entity.vo.AuthTokenVO;
import com.magicrepokit.system.service.IAuthService;
import com.magicrepokit.system.service.IUserService;
import com.magicrepokit.system.vo.UserInfo;
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
    private RestTemplate restTemplate;
    @Value("${mrk.client-id}")
    private String clientId;
    @Value("${mrk.client-secret}")
    private String clientSecret;

    /**
     *  登录
     *
     * @param loginDTO 登录信息
     * @return 登录信息
     */
    @Override
    public AuthTokenVO login(LoginDTO loginDTO) {
        //校验用户
        UserInfo authenticate = authenticate(loginDTO.getUsername(), loginDTO.getPassword());
        //oauth登录获取令牌

        return remoteTokenService(JWTConstant.PASSWORD, clientId, clientSecret, loginDTO.getUsername(), loginDTO.getPassword(), null);
    }

    /**
     * 申请令牌
     */
    private AuthTokenVO remoteTokenService(String grantType,String clientId,String clientSecret,String username,String password,String refreshToken){
        HttpServletRequest request = WebUtil.getRequest();
        String userType = request.getHeader(JWTConstant.USER_TYPE);
        if(userType==null){
            throw new ServiceException(SystemResultCode.NOT_FOUND_USER_TYPE);
        }
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
     * 验证账户密码
     *
     * @param username
     * @param password
     * @return
     */
    public UserInfo authenticate(String username,String password){
        //查询用户信息
        UserInfo userInfo = userService.userInfo(username);
        if(ObjectUtil.isEmpty(userInfo)){
            throw new ServiceException(SystemResultCode.NOT_FOUND_USER);
        }
        //校验密码
        if (!userService.isPasswordMatch(password,userInfo.getUser().getPassword())) {
            throw new ServiceException(SystemResultCode.NOT_FOUND_USER);
        }
        //判断是否激活
        if(userInfo.getUser().getStatus()== SystemUserStatus.Disabled.getCode()){
            throw new ServiceException(SystemResultCode.DISABLED_USER);
        }
        return userInfo;
    }
}
