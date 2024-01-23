package com.magicrepokit.gateway.provider;

import java.util.ArrayList;
import java.util.List;

public class AuthProvider {
    private static final List<String> DEFAULT_SKIP_URL = new ArrayList<>();

    static {
        DEFAULT_SKIP_URL.add("/system/auth/login");
        DEFAULT_SKIP_URL.add("/system/auth/refresh-token/**");
        DEFAULT_SKIP_URL.add("/system/auth/social-login-redirect/**");
        DEFAULT_SKIP_URL.add("/system/auth/social-login");
        DEFAULT_SKIP_URL.add("/**/v2/api-docs/**");
        DEFAULT_SKIP_URL.add("/system/auth/register");
        DEFAULT_SKIP_URL.add("/system/auth/forget-password");
        DEFAULT_SKIP_URL.add("/system/auth/send-code");
    }


    /**
     * 默认无需鉴权的API
     * @return
     */
    public static List<String> getDefaultSkipUrl() {
        return DEFAULT_SKIP_URL;
    }
}
