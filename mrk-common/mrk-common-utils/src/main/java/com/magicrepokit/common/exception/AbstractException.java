package com.magicrepokit.common.exception;
/**
 * <p>Description: 抽象异常类 </p>
 * @date 2020/1/2
 * @author 贺锟 
 * @version 1.0
 * <p>Copyright:Copyright(c)2019</p>
 */
public abstract class AbstractException extends Exception {
    private static final long serialVersionUUID = -1L;

    public AbstractException(String message) {
        super(message);
    }

}
