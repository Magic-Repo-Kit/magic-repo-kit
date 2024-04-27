package com.magicrepokit.system.vo.auth;

import com.magicrepokit.mb.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SocialUserAuthVO extends BaseEntity {
    /**
     * 社交用户 openid
     */
    private String openid;

    /**
     * 关联的用户编号
     */
    private Long userId;
}
