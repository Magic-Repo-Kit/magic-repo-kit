package com.magicrepokit.gateway.provider;

import java.util.ArrayList;
import java.util.List;

public class AuthProvider {
    private static final List<String> DEFAULT_SKIP_URL = new ArrayList<>();

    static {
        DEFAULT_SKIP_URL.add("/system/auth/**");
        DEFAULT_SKIP_URL.add("/**/v2/api-docs/**");
    }


    /**
     * 默认无需鉴权的API
     * @return
     */
    public static List<String> getDefaultSkipUrl() {
        return DEFAULT_SKIP_URL;
    }
}
