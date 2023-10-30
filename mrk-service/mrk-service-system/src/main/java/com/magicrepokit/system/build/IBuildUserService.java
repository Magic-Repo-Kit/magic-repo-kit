package com.magicrepokit.system.build;

import com.magicrepokit.system.entity.User;
import com.magicrepokit.system.entity.vo.UserInfoVO;

public interface IBuildUserService {
    /**
     * 根据用户获取用户所有信息
     * @param user
     * @return
     */
    UserInfoVO userInfoBuild (User user);
}
