package com.magicrepokit.auth.constant;

import lombok.AllArgsConstructor;
import org.omg.CORBA.UNKNOWN;

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
    USER_NOT_FOUND("USER_NOT_FOUND"),

    /**
     * 未知授权类型
     */
    UNKNOWN_GRANT_TYPE("UNKNOWN_GRANT_TYPE"),

    /**
     * 未发现Authorization
     */
    NOT_FOUND_AUTHORIZATION("NOT_FOUND_AUTHORIZATION"),

    /**
     * 未发现客户端
     */
    NOT_FOUND_CLIENT("NOT_FOUND_CLIENT"),

    /**
     * 令牌不能为空
     */
    NOT_EMPTY_ACCESS_TOKEN("NOT_EMPTY_ACCESS_TOKEN"),

    ;



    private final String message;

    public String getMessage(){
        //TODO 增加多语言返回
        return this.message;
    }
}
