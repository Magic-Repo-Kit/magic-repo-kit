package com.gpt.log.exceotion.handler;

import com.gpt.common.api.R;
import com.gpt.common.api.ResultCode;
import com.gpt.log.exceotion.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceotionHandler {

    /**
     * 处理业务异常 ServiceException
     *
     * 例如说，商品库存不足，用户手机号已存在。
     */
    @ExceptionHandler(value = ServiceException.class)
    public R<?> serviceExceptionHandler(ServiceException ex) {
        log.info("[serviceExceptionHandler]", ex);
        return R.fail(ex.getResultCode(), ex.getMessage());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public R<?> httpMessageNotReadableHandler(HttpMessageNotReadableException ex) {
        log.info("[httpMessageNotReadableHandler]", ex);
        return R.fail(ResultCode.PARAM_BIND_ERROR, ResultCode.PARAM_BIND_ERROR.getMessage());
    }

    /**
     * 未知异常处理
     * @param req
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public R<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex){
        //插入异常日志
        log.info("[serviceExceptionHandler]", ex);
        return R.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(),ResultCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}
