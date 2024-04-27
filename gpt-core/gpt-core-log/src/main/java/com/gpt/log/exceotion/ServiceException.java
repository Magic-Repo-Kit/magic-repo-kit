package com.gpt.log.exceotion;

import cn.hutool.core.util.StrUtil;
import com.gpt.common.api.IResultCode;
import com.gpt.common.api.ResultCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 2359767895161832954L;

    @Getter
    private final IResultCode resultCode;

    public ServiceException(String message) {
        super(message);
        this.resultCode = ResultCode.INTERNAL_SERVER_ERROR;
    }

    public ServiceException(IResultCode resultCode,String message) {
        super(resultCode.getMessage().contains("{}")?StrUtil.format(resultCode.getMessage(),message):message);
        this.resultCode = resultCode;
    }

    public ServiceException(IResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public ServiceException(IResultCode resultCode, String ...args) {
        super(doFormat(resultCode.getMessage(), (Object) args));
        this.resultCode = resultCode;
    }

    public ServiceException(IResultCode resultCode, Throwable cause) {
        super(cause);
        this.resultCode = resultCode;
    }


    private static String doFormat(String messagePattern, Object... params) {
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int i = 0;
        int j;
        int l;
        for (l = 0; l < params.length; l++) {
            j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                log.error("[doFormat][参数过多:错误内容({})|参数({})", messagePattern, params);
                if (i == 0) {
                    return messagePattern;
                } else {
                    sbuf.append(messagePattern.substring(i));
                    return sbuf.toString();
                }
            } else {
                sbuf.append(messagePattern, i, j);
                sbuf.append(params[l]);
                i = j + 2;
            }
        }
        if (messagePattern.indexOf("{}", i) != -1) {
            log.error("[doFormat][参数过少：错误内容({})|参数({})", messagePattern, params);
        }
        sbuf.append(messagePattern.substring(i));
        return sbuf.toString();
    }

    /**
     * 提高性能
     *
     * @return Throwable
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public Throwable doFillInStackTrace() {
        return super.fillInStackTrace();
    }
}
