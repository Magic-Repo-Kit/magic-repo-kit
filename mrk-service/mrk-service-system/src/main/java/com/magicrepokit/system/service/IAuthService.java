package com.magicrepokit.system.service;

import com.magicrepokit.system.entity.dto.AuthLoginDTO;
import com.magicrepokit.system.entity.dto.AuthSocialLoginDTO;
import com.magicrepokit.system.entity.vo.AuthTokenVO;

import javax.validation.Valid;

/**
 * 用户认证登录接口
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
public interface IAuthService {

    /**
     * 登录
     *
     * @param authLoginDTO 登录信息
     * @return 登录结果
     */
    AuthTokenVO login(@Valid AuthLoginDTO authLoginDTO);

    /**
     * 刷新token
     *
     * @param refreshToken
     * @return
     */
    AuthTokenVO refreshToken(String refreshToken);

    /**
     *  获取三方认证请求地址
     *
     * @param type 三方平台 10：github 20:google 30:gitee
     * @param redirectUri 本系统回掉地址
     * @return 三方请求地址
     */
    String socialLoginRedirect(Integer type, String redirectUri);

    /**
     * 三方快捷登录换取本系统token
     *
     * @param authSocialLoginDTO 三方code相关信息
     * @return 令牌信息
     */
    AuthTokenVO socialLogin(AuthSocialLoginDTO authSocialLoginDTO);
}
