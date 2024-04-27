package com.magicrepokit.gateway.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum GateWayResult {

    NOT_FOUND_JWT("NOT_FOUND_JWT"),

    UNAUTHORIZED("UNAUTHORIZED"),
    NOT_FOUND_USER_TYPE("NOT_FOUND_USER_TYPE"),
    INVALID_TOKEN("INVALID_TOKEN")
    ;


    private final String message;

    public String getMessage(){
        //TODO 增加多语言返回
        return this.message;
    }

}
