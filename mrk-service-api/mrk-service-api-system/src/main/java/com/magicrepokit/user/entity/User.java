package com.magicrepokit.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.magicrepokit.mp.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户实体类
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mrk_system_user")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    /**
     * 用户编号
     */
    private String code;

    /**
     * 用户平台[0:本地平台,1:github]
     */
    private Integer userType;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String name;

    /**
     * 真名
     */
    private String realName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String phone;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 岗位id
     */
    private String postId;
}
