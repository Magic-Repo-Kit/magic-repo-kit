package com.magicrepokit.jwt.constant;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserTypeEnum {
    APP("app",1), // app模式
    PC("pc",2), // pc模式
    ;

    private final String userType;

    private final int code;

    public static UserTypeEnum getByUserType(String userType) {
        return ArrayUtil.firstMatch(o -> o.getUserType().equals(userType), values());
    }
}
