package com.magicrepokit.user.build.impl;

import com.magicrepokit.common.utils.ObjectUtil;
import com.magicrepokit.user.build.IBuildUserService;
import com.magicrepokit.user.entity.User;
import com.magicrepokit.user.vo.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class BuildUserServiceImpl implements IBuildUserService {
    /**
     * 根据用户获取用户所有信息
     * @param user
     * @return
     */
    @Override
    public UserInfo userInfoBuild(User user) {
        if(ObjectUtil.isEmpty(user)){
            return null;
        }
        //扩展用户其它信息

        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);
        return userInfo;
    }
}
