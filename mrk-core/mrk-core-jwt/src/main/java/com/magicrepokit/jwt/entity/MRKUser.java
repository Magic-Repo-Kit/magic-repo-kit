package com.magicrepokit.jwt.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MRKUser {
    /**
     * 用户id
     */
    private final Long userId;
    /**
     * 昵称
     */
    private final String name;
    /**
     * 真名
     */
    private final String realName;
    /**
     * 账号
     */
    private final String account;
    /**
     * 部门id
     */
    private final String deptId;
    /**
     * 岗位id
     */
    private final String postId;
    /**
     * 角色id
     */
    private final String roleId;
}
