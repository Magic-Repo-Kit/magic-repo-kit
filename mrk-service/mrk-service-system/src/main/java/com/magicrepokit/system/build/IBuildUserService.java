package com.magicrepokit.system.build;

import com.magicrepokit.system.entity.User;
import com.magicrepokit.system.vo.UserInfo;

public interface IBuildUserService {
    /**
     * 根据用户获取用户所有信息
     * @param user
     * @return
     */
    UserInfo userInfoBuild (User user);
}
