package com.magicrepokit.common.exception.constants;
/**
 * <p>Description: </p>
 * @author
 * @version 1.0
 * <p>Copyright:Copyright(c)2019</p>
 */
public interface IErrorCodeEnum {

     // 定义服务模块名称, 标识是哪个服务抛出的错误码
    /**
     * 用户服务模块
     */
    String MODULE_USER = "USER_";

    /**
     * 系统模块
     */
    String MODULE_SYSTEM = "SYS_";


    /**
     * 获取自定义的错误码
     * @return
     */
    public String getCode();

    /**
     * 获取自定义的错误码提示消息
     * @return
     */
    public String getMessage();

    /**
     * 获取自定义的错误级别信息
     * @return
     */
    public WarningLevelEnum getLevel();

}
