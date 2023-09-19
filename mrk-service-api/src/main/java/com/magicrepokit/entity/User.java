package com.magicrepokit.entity;

import com.magicrepokit.mp.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends BaseEntity {
    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户密码
     */
    private String userPwd;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 最近一次用户登陆IP
     */
    private String lastLoginIp;

    /**
     * 最近一次登陆时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 状态(0:有效， 1：锁定， 2：禁用）
     */
    private int status;

}
