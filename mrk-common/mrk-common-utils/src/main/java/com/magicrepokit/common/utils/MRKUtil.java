package com.magicrepokit.common.utils;

import org.springframework.lang.Nullable;

public class MRKUtil {

    /**
     * 字符串转 int，为空则返回默认值
     * @param str  字符串
     * @param defaultValue 默认值
     * @return int
     */
    public static int toInt(@Nullable final Object str,final int defaultValue){
        return NumberUtil.toInt(String.valueOf(str),defaultValue);
    }
}
