package com.magicrepokit.chat.constant;

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
public enum ChatResultCode implements IResultCode {
    //=================系统用户登录相关=======================

    //=====异常级别================
    //未发现用户
    SERVICE_ERROR(20010301,"SERVICE_ERROR"),
    ;

    /**
     * code编码
     */
    final int code;

    /**
     * 多语言信息key
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
