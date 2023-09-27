package com.magicrepokit.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.mp.base.BaseServiceImpl;
import com.magicrepokit.user.build.IUserBuildService;
import com.magicrepokit.user.entity.User;
import com.magicrepokit.user.mapper.UserMapper;
import com.magicrepokit.user.service.IUserService;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private IUserBuildService userBuild;

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
