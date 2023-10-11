package com.magicrepokit.user.service;

import com.magicrepokit.mp.base.BaseService;
import com.magicrepokit.user.entity.User;
import com.magicrepokit.user.vo.UserInfo;

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
    UserInfo userInfo(Long userId);

    /**
     * 用户信息
     * @param account
     * @return
     */
    UserInfo userInfo(String account,Integer userType);
}
