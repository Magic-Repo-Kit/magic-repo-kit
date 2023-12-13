package com.magicrepokit.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.log.exceotion.ServiceException;
import com.magicrepokit.mb.base.BaseServiceImpl;
import com.magicrepokit.system.build.IBuildUserService;
import com.magicrepokit.system.constant.SystemResultCode;
import com.magicrepokit.system.entity.User;
import com.magicrepokit.system.mapper.UserMapper;
import com.magicrepokit.system.service.IUserService;
import com.magicrepokit.system.entity.vo.UserInfoVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements IUserService {
    private IBuildUserService userBuild;

    /**
     * 用户信息
     * @param userId
     * @return
     */
    @Override
    public UserInfoVO userInfo(Long userId) {
        User user = this.getById(userId);
        return userBuild.userInfoBuild(user);
    }

    /**
     * 用户信息
     * @param account
     * @return
     */
    @Override
    public UserInfoVO userInfo(String account) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getAccount,account)
        );
        return userBuild.userInfoBuild(user);
    }

    /**
     * 密码校验
     *
     * @param rawPassword 未加密的密码
     * @param encodedPassword 加密后的密码
     * @return
     */
    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword,encodedPassword);
    }

    /**
     * 用户创建
     *
     * @param user
     * @return
     */
    @Override
    public boolean createUser(User user) {
        //检测用户名是否存在
        if(checkAccount(user.getAccount())){
            throw new ServiceException(SystemResultCode.USERNAME_EXIST);
        }
        this.save(user);
        return true;
    }

    /**
     * 检测用户名
     * @param account 用户名
     * @return 是否存在
     */
    private boolean checkAccount(String account){
        return this.getOne(new LambdaQueryWrapper<User>().eq(User::getAccount,account))!=null;
    }
}
