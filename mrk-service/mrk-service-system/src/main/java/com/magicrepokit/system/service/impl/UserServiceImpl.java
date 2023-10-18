package com.magicrepokit.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.mp.base.BaseServiceImpl;
import com.magicrepokit.system.build.IBuildUserService;
import com.magicrepokit.system.entity.User;
import com.magicrepokit.system.mapper.UserMapper;
import com.magicrepokit.system.service.IUserService;
import com.magicrepokit.system.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private IBuildUserService userBuild;

    /**
     * 用户信息
     * @param userId
     * @return
     */
    @Override
    public UserInfo userInfo(Long userId) {
        User user = this.getById(userId);
        return userBuild.userInfoBuild(user);
    }

    /**
     * 用户信息
     * @param account
     * @return
     */
    @Override
    public UserInfo userInfo(String account) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getAccount,account)
        );
        return userBuild.userInfoBuild(user);
    }
}