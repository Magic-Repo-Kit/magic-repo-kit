package com.gpt.system.entity.social;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gpt.mb.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 社交用户信息
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "social_user")
@Accessors(chain = true)
public class SocialUser extends BaseEntity {
    /**
     * 社交平台的类型
     *
     */
    private Integer type;

    /**
     * 社交 openid
     */
    private String openid;
    /**
     * 社交 token
     */
    private String token;
    /**
     * 原始 Token 数据，一般是 JSON 格式
     */
    private String rawTokenInfo;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 原始用户数据，一般是 JSON 格式
     */
    private String rawUserInfo;

    /**
     * 最后一次的认证 code
     */
    private String code;
    /**
     * 最后一次的认证 state
     */
    private String state;
}
