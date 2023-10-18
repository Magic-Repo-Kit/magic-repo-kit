package com.magicrepokit.jwt.constant;

/**
 * JWT配置常量
 */
public interface JWTConstant {
    /**
     * 密钥的最小长度
     */
    int SECRET_KEY_LENGTH = 32;
    //==================令牌内容相关字段========================
    String CLIENT_ID = "client_id";
    String USER_NAME = "user_name";
    String NICK_NAME = "nick_name";
    String REAL_NAME = "real_name";
    String USER_ID = "user_id";
    String DEPT_ID = "dept_id";
    String POST_ID = "post_id";
    String ROLE_ID = "role_id";
    String ROLE_NAME = "role_name";
    String TENANT_ID = "tenant_id";
    String ACCOUNT = "account";
    //==================redis相关字段========================
    String REDIS_KEY_ACCESS_TOKEN = "mrk:accessToken:";
    String REDIS_KEY_REFRESH_TOKEN = "mrk:refreshToken:";
    //=================令牌设置-=============================
    int ACCESS_TOKEN_VALIDITY_SECONDS = 7200;

    int REFRESH_TOKEN_VALIDITY_SECONDS = 3600*24*7;
}
