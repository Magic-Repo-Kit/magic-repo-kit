package com.gpt.system.service;

import com.gpt.mb.base.BaseService;
import com.gpt.system.dto.auth.UserForgetPassword;
import com.gpt.system.dto.auth.UserRegister;
import com.gpt.system.entity.user.User;
import com.gpt.system.vo.user.UserInfoVO;

/**
 * user服务类
 */
public interface IUserService extends BaseService<User> {

    /**
     * 用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserInfoVO userInfo(Long userId);

    /**
     * 用户信息
     * @param account 账户
     * @return 用户信息
     */
    UserInfoVO userInfo(String account);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword 未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 创建用户
     * @param user 用户信息
     * @return 是否创建成功
     */
    boolean createUser(User user);

    /**
     * 检查账户是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean checkEmail(String email);

    /**
     * 注册
     * @param userRegister 注册信息
     * @return 是否成功
     */
    boolean register(UserRegister userRegister);

    /**
     * 检查账户是否存在
     * @param account 账户
     * @return 是否存在
     */
    boolean checkAccount(String account);

    /**
     * 忘记密码
     * @param userForgetPassword 忘记密码信息
     * @return 是否成功
     */
    boolean forgetPassword(UserForgetPassword userForgetPassword);

    /**
     * 查询用户信息
     * @param email 邮箱
     * @return 用户信息
     */
    UserInfoVO userInfoByEmail(String email);
}
