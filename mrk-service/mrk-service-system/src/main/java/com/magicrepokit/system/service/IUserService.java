package com.magicrepokit.system.service;

import com.magicrepokit.mb.base.BaseService;
import com.magicrepokit.system.dto.auth.UserRegister;
import com.magicrepokit.system.entity.user.User;
import com.magicrepokit.system.vo.user.UserInfoVO;

/**
 * user服务类
 */
public interface IUserService extends BaseService<User> {

    /**
     * 用户信息
     *
     * @param userId
     * @return
     */
    UserInfoVO userInfo(Long userId);

    /**
     * 用户信息
     * @param account
     * @return
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

    boolean register(UserRegister userRegister);

    boolean checkAccount(String account);
}
