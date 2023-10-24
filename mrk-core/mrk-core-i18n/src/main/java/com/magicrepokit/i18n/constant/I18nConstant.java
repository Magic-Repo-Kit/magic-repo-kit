package com.magicrepokit.i18n.constant;

public interface I18nConstant {
    /**
     * 多语言的请求头
     */
    String LOCALE = "Accept-Language";

    /**
     * redis基础key
     */
    String REDIS_LOCALE_MESSAGE_KEY = "mkr:i18n:";



    String BASE_RESOURCE = "i18n/message";

    String APP_RESOURCE ="i18n";

    /**
     * 返回i18n的redis key
     * @param appName
     * @return
     */
    static String i18nKey(String appName) {
        return REDIS_LOCALE_MESSAGE_KEY + appName;
    }
}
