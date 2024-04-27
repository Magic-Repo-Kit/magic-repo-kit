package com.gpt.system.entity.social;


import com.baomidou.mybatisplus.annotation.TableName;
import com.gpt.mb.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("social_user_bind")
@Builder
public class SocialUserBind extends BaseEntity {
    /**
     * 关联的用户编号
     *
     * 关联 UserDO 的编号
     */
    private Long userId;
    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 社交平台的用户编号
     *
     */
    private Long socialUserId;

    /**
     * 社交平台的类型
     *
     */
    private Integer socialType;
}
