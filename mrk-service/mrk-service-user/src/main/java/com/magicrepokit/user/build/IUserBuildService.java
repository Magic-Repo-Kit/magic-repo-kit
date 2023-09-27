package com.magicrepokit.user.build;

import com.magicrepokit.user.entity.User;
import com.magicrepokit.user.vo.UserInfo;

public interface IUserBuildService {
    /**
     * 根据用户获取用户所有信息
     * @param user
     * @return
     */
    UserInfo userInfoBuild (User user);
}
