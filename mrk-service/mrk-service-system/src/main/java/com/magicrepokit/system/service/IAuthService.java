package com.magicrepokit.system.service;

import com.magicrepokit.system.dto.auth.AuthLoginDTO;
import com.magicrepokit.system.dto.auth.AuthSocialLoginDTO;
import com.magicrepokit.system.dto.auth.UserForgetPassword;
import com.magicrepokit.system.dto.auth.UserRegister;
import com.magicrepokit.system.vo.auth.AuthTokenVO;

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

    /**
     * 用户注册
     * @param userRegister 注册信息
     * @return 注册结果
     */
    Boolean register(UserRegister userRegister);

    /**
     * 发送验证码
     * @param type 1:注册 2:忘记密码
     * @param email 邮箱
     * @return 是否发送成功
     */
    Boolean sendCode(Integer type, String email);

    /**
     * 忘记密码
     * @param userForgetPassword 忘记密码信息
     * @return 是否修改成功
     */
    boolean forgetPassword(UserForgetPassword userForgetPassword);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean checkEmail(String email);

    /**
     * 检查账户是否存在
     * @param account 账户
     * @return 是否存在
     */
    boolean checkAccount(String account);
}
