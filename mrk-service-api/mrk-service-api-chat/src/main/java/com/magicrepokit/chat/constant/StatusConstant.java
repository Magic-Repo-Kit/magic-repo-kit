package com.magicrepokit.chat.constant;

public interface StatusConstant {
    //-------------------------------UserGPT---------------------------
    //1:正常,2:无账户,3:无普通额度,4:无订阅额度,5:自己的账户
    /**
     * 正常
     */
    Integer GPT_NORMAL = 1;
    /**
     * 无账户
     */
    Integer GPT_NO_ACCOUNT = 2;
    /**
     * 无普通额度
     */
    Integer GPT_NO_REGULAR_CREDIT_LIMIT = 3;
    /**
     * 无订阅额度
     */
    Integer GPT_NO_SUBSCRIPTION_CREDIT_LIMIT = 4;
    /**
     * 自己的账户
     */
    Integer GPT_SELF_ACCOUNT = 5;
    Integer YES = 2;

    Integer NO = 1;
}
