package com.magicrepokit.auth.utils;

import cn.hutool.core.util.StrUtil;

import java.util.List;

public class AuthUtils {
    public static List<String> buildScopes(String scope) {
        return StrUtil.split(scope, ' ');
    }
}
