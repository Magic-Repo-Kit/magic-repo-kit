package com.magicrepokit.auth.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MRKI18N {

    /**
     * 未发现用户登录类型
     */
    NOT_FOUND_USER_TYPE("NOT_FOUND_USER_TYPE"),

    /**
     * 用户已被锁定
     */
    USER_IS_LOCKED("USER_IS_LOCKED"),

    /**
     * 用户名或密码错误
     */
    USER_NOT_FOUND("USER_NOT_FOUND");



    private final String message;

    public String getMessage(){
        //TODO 增加多语言返回
        return this.message;
    }
}
