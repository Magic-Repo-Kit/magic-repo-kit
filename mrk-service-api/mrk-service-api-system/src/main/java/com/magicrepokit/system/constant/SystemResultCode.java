package com.magicrepokit.system.constant;

import com.magicrepokit.common.api.IResultCode;
import com.magicrepokit.i18n.utils.MessageUtil;
import lombok.AllArgsConstructor;

/**
 * 状态码常量
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 * 状态码构成：服务码(xx)模块(xx)业务状态(01:正常 02:警告 03:异常(xx))类别序号(xx)
 */
@AllArgsConstructor
public enum SystemResultCode implements IResultCode {
    //=================系统用户登录相关=======================

    //=====异常级别================
    //未发现用户
    NOT_FOUND_USER(10010301,"NOT_FOUND_USER"),
    //远程服务调用异常
    REMOTE_SERVICE_ERROR(10010302,"REMOTE_SERVICE_ERROR" ),
    //未找到服务
    NOT_FOUND_SERVICE(10010303,"NOT_FOUND_SERVICE" ),
    CREATE_JWT_FAILED(10010304, "CREATE_JWT_FAILED"),

    //=====警告级别===============
    //用户被禁用
    DISABLED_USER(10010201, "DISABLED_USER"),
    //未发现USER-TYPE
    NOT_FOUND_USER_TYPE(10010202,"NOT_FOUND_USER_TYPE"),
    ;



    /**
     * code编码
     */
    final int code;

    /**
     * 中文信息描述
     */
    final String message;

    @Override
    public String getMessage() {
        return MessageUtil.getMessage(this.message);
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
