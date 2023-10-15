package com.magicrepokit.auth.constant;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MRKAuthGrantTypeEnum {
    PASSWORD("password"), // 密码模式
    AUTHORIZATION_CODE("code"), // 授权码模式
    //IMPLICIT("implicit"), // 简化模式(禁用)
    CLIENT_CREDENTIALS("client"), // 客户端模式
    REFRESH_TOKEN("refresh_token"), // 刷新模式
    ;

    private final String grantType;

    public static MRKAuthGrantTypeEnum getByGranType(String grantType) {
        return ArrayUtil.firstMatch(o -> o.getGrantType().equals(grantType), values());
    }

}
