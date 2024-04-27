package com.gpt.system.build.impl;

import cn.hutool.core.util.ObjectUtil;
import com.gpt.system.build.IBuildUserService;
import com.gpt.system.entity.user.User;
import com.gpt.system.vo.user.UserInfoVO;
import org.springframework.stereotype.Service;

@Service
public class BuildUserServiceImpl implements IBuildUserService {
    /**
     * 根据用户获取用户所有信息
     * @param user
     * @return
     */
    @Override
    public UserInfoVO userInfoBuild(User user) {
        if(ObjectUtil.isEmpty(user)){
            return null;
        }
        //扩展用户其它信息

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUser(user);
        return userInfoVO;
    }
}
