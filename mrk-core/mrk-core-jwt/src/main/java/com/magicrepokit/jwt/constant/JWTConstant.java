package com.magicrepokit.jwt.constant;

/**
 * JWT配置常量
 */
public interface JWTConstant {

    //==================令牌配置相关========================
    /**
     * 密钥的最小长度
     */
    int SECRET_KEY_LENGTH = 32;
    /**
     * 刷新请求token过期时间
     */
    int ACCESS_TOKEN_VALIDITY_SECONDS = 7200;
    /**
     * 刷新token过期时间
     */
    int REFRESH_TOKEN_VALIDITY_SECONDS = 3600*24*7;
    //==================令牌增强内容相关字段========================
    String CLIENT_ID = "client_id";
    String USERNAME = "username";
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
    //=================oauth请求参数相关-=============================
    String PASSWORD = "password";
    String GRANT_TYPE = "grant_type";
    String REFRESH_TOKEN = "refresh_token";
    String AUTHORIZATION = "Authorization";
    String USER_TYPE = "User-Type";
    String ACCESS_TOKEN = "access_token";
    String JTI = "jti";
}
