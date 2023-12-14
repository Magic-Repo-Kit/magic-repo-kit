package com.magicrepokit.system.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenVO {
    /**
     * 请求token
     */
    private String access_token;
    /**
     * token类型
     */
    private String token_type;
    /**
     * 刷新token
     */
    private String refresh_token;
    /**
     * 过期时间
     */
    private String expires_in;
    /**
     * 资源服务器范围
     */
    private String scope;
    /**
     * 岗位
     */
    private Long post_id;
    /**
     * 用户id
     */
    private Long user_id;
    /**
     * 角色id
     */
    private Long role_id;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 账户
     */
    private String account;
    /**
     * 令牌身份id
     */
    private String jti;
}
