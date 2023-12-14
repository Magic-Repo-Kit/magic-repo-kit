package com.magicrepokit.auth.service;

import com.magicrepokit.system.vo.user.UserInfoVO;
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

    public MrkUserDetails(UserInfoVO userInfoVO, Collection<? extends GrantedAuthority> authorities){
        super(userInfoVO.getUser().getAccount(), userInfoVO.getUser().getPassword(),true,true,true,true,authorities);
        this.userId = userInfoVO.getUser().getId();
        this.name = userInfoVO.getUser().getName();
        this.realName = userInfoVO.getUser().getRealName();
        this.account = userInfoVO.getUser().getAccount();
        this.deptId = userInfoVO.getUser().getDeptId();
        this.postId = userInfoVO.getUser().getPostId();
        this.roleId = userInfoVO.getUser().getRoleId();
    }
}
