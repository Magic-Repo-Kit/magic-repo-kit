package com.gpt.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gpt.log.exceotion.ServiceException;
import com.gpt.mb.base.BaseServiceImpl;
import com.gpt.system.build.IBuildUserService;
import com.gpt.system.constant.SystemResultCode;
import com.gpt.system.converter.UserConverter;
import com.gpt.system.dto.auth.UserForgetPassword;
import com.gpt.system.dto.auth.UserRegister;
import com.gpt.system.entity.user.User;
import com.gpt.system.mapper.UserMapper;
import com.gpt.system.service.IUserService;
import com.gpt.system.vo.user.UserInfoVO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements IUserService {
    private IBuildUserService userBuild;
    private UserConverter userConverter;

    /**
     * 用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    @Override
    public UserInfoVO userInfo(Long userId) {
        User user = this.getById(userId);
        return userBuild.userInfoBuild(user);
    }

    /**
     * 用户信息
     * @param account 账户
     * @return 用户信息
     */
    @Override
    public UserInfoVO userInfo(String account) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getAccount,account)
        );
        return userBuild.userInfoBuild(user);
    }

    @Override
    public UserInfoVO userInfoByEmail(String email) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail,email)
        );
        return userBuild.userInfoBuild(user);
    }

    /**
     * 密码校验
     *
     * @param rawPassword 未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword,encodedPassword);
    }

    /**
     * 用户创建
     *
     * @param user 用户信息
     * @return 是否创建成功
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
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    @Override
    public boolean checkEmail(String email) {
        //检测邮箱是否存在
        return this.count(new LambdaQueryWrapper<User>().eq(User::getEmail,email))>0;
    }

    /**
     * 注册
     * @param userRegister 注册信息
     * @return 是否成功
     */
    @Override
    public boolean register(UserRegister userRegister) {
        User user = userConverter.registerDTO2User(userRegister);
        user.setPassword(BCrypt.hashpw(user.getPassword(),BCrypt.gensalt()));
        List<Integer> userType = new ArrayList<>();
        userType.add(1);
        userType.add(2);
        user.setUserType(userType);
        user.setStatus(1);
        return this.createUser(user);
    }

    /**
     * 检测用户名
     * @param account 用户名
     * @return 是否存在
     */
    public boolean checkAccount(String account){
        return this.count(new LambdaQueryWrapper<User>().eq(User::getAccount,account))>0;
    }

    /**
     * 忘记密码
     * @param userForgetPassword 忘记密码信息
     * @return 是否修改成功
     */
    @Override
    public boolean forgetPassword(UserForgetPassword userForgetPassword) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, userForgetPassword.getEmail()));
        if(user!=null){
            user.setPassword(BCrypt.hashpw(userForgetPassword.getPassword(),BCrypt.gensalt()));
            return this.updateById(user);
        }
        return false;
    }
}
