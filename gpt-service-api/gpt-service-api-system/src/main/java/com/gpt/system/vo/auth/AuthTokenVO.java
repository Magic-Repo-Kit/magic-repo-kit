package com.gpt.system.vo.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "登录返回实体", description = "登录返回实体")
public class AuthTokenVO {
    /**
     * 请求token
     */
    @ApiModelProperty(value = "请求token")
    private String access_token;
    /**
     * token类型
     */
    @ApiModelProperty(value = "token类型")
    private String token_type;
    /**
     * 刷新token
     */
    @ApiModelProperty(value = "刷新token")
    private String refresh_token;
    /**
     * 过期时间
     */
    @ApiModelProperty(value = "过期时间")
    private String expires_in;
    /**
     * 资源服务器范围
     */
    @ApiModelProperty(value = "资源服务器范围")
    private String scope;
    /**
     * 岗位
     */
    @ApiModelProperty(value = "岗位")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long post_id;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long user_id;
    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long role_id;
    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dept_id;
    /**
     * 账户
     */
    @ApiModelProperty(value = "账户")
    private String account;
    /**
     * 令牌身份id
     */
    @ApiModelProperty(value = "令牌身份id")
    private String jti;
}
