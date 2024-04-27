package com.gpt.system.service;

import com.gpt.system.entity.social.SocialUser;
import com.gpt.system.vo.auth.SocialUserAuthVO;

/**
 * 社交账号管理
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
public interface ISocialUserService {
    /**
     * 获取社交账户信息
     * @param type 账户类型
     * @param code 授权码
     * @param state 状态码
     * @return 社交账户信息
     */
    SocialUser getSocialUser(Integer type, String code, String state);

    SocialUserAuthVO authSocialUser(Integer type, String code, String state);
}
