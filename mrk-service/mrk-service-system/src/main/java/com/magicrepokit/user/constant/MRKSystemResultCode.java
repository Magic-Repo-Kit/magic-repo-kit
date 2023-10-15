package com.magicrepokit.user.constant;

import cn.hutool.core.lang.intern.InternUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.magicrepokit.common.api.IResultCode;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.text.StrBuilder;

/**
 * 状态码常量
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 * 状态码构成：服务码(xx)模块(xx)业务状态(01:正常 02:警告 03:异常(xx))类别序号(xx)
 */
@AllArgsConstructor
public enum MRKSystemResultCode implements IResultCode {


    /**
     * 客户端不存在
     */
    OAUTH2_CLIENT_NOT_EXISTS(10010201,"OAUTH2_CLIENT_NOT_EXISTS"),

    /**
     * 客户端被禁用
     */
    OAUTH2_CLIENT_DISABLE(10010202,"OAUTH2_CLIENT_DISABLE"),

    /**
     * 客户端密钥错误
     */
    OAUTH2_CLIENT_CLIENT_SECRET_ERROR(10010203,"OAUTH2_CLIENT_CLIENT_SECRET_ERROR"),

    /**
     * 不支持该授权类型
     */
    OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS(10010204,"OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS"),

    OAUTH2_CLIENT_SCOPE_OVER(1_002_020_004, "OAUTH2_CLIENT_SCOPE_OVER"),
    OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH(1_002_020_005, "OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH:{}");

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
        //TODO 后期改造i18n
        return this.getMessage();
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
