package com.magicrepokit.system.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SystemI18N {

    /**
     * 账户不能为空
     */
    ACCOUNT_CANNOT_BE_EMPTY("ACCOUNT_CANNOT_BE_EMPTY")
    ;


    private final String message;

    public String getMessage(){
        //TODO 增加多语言返回
        return this.message;
    }
}
