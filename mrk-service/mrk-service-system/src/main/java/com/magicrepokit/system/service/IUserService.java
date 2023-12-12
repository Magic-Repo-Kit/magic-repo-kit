package com.magicrepokit.system.service;

import com.magicrepokit.mb.base.BaseService;
import com.magicrepokit.system.entity.User;
import com.magicrepokit.system.entity.vo.UserInfoVO;

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
     * @param user
     * @return
     */
    boolean createUser(User user);
}
