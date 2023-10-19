package com.magicrepokit.system.build.impl;

import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.system.build.IBuildUserService;
import com.magicrepokit.system.entity.User;
import com.magicrepokit.system.vo.UserInfo;
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
