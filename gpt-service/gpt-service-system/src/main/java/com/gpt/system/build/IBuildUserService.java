package com.gpt.system.build;

import com.gpt.system.entity.user.User;
import com.gpt.system.vo.user.UserInfoVO;

public interface IBuildUserService {
    /**
     * 根据用户获取用户所有信息
     * @param user
     * @return
     */
    UserInfoVO userInfoBuild (User user);
}
