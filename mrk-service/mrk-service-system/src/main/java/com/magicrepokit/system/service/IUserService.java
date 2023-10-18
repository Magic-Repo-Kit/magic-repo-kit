package com.magicrepokit.system.service;

import com.magicrepokit.mp.base.BaseService;
import com.magicrepokit.system.entity.User;
import com.magicrepokit.system.vo.UserInfo;

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
    UserInfo userInfo(String account);
}
