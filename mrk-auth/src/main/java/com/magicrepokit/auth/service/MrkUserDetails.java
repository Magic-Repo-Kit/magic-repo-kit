package com.magicrepokit.auth.service;

import com.magicrepokit.auth.constant.MRKAuthConstant;
import com.magicrepokit.user.vo.UserInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 认证用户增强
 */
@Getter
public class MrkUserDetails extends User {
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

    public MrkUserDetails(Long userId, String name, String realName, String deptId, String postId, String roleId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.name = name;
        this.realName = realName;
        this.account = username;
        this.deptId = deptId;
        this.postId = postId;
        this.roleId = roleId;
    }

    public MrkUserDetails(UserInfo userInfo,Collection<? extends GrantedAuthority> authorities){
        super(userInfo.getUser().getAccount(),userInfo.getUser().getPassword(),true,true,true,true,authorities);
        this.userId = userInfo.getUser().getId();
        this.name = userInfo.getUser().getName();
        this.realName = userInfo.getUser().getRealName();
        this.account = userInfo.getUser().getAccount();
        this.deptId = userInfo.getUser().getDeptId();
        this.postId = userInfo.getUser().getPostId();
        this.roleId = userInfo.getUser().getRoleId();
    }
}
