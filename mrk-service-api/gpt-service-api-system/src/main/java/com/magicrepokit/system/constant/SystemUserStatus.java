package com.magicrepokit.system.constant;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemUserStatus {
    //状态:[0:未激活 1:初始 2:激活 3:禁用]
    NOT_ACTIVATED(0),
    INITIAL(1),
    ACTIVATED(2),
    Disabled(3)
    ;


    private final int code;

    public static SystemUserStatus  getUserStatus(Integer code) {
        return ArrayUtil.firstMatch(o -> o.code==code, values());
    }
}
