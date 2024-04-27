package com.gpt.common.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.NumberUtils;

public class NumberUtil extends NumberUtils {

    /**
     * string转int，若String为空或转换异常，返回默认值
     * @param str string值
     * @param defaultValue 默认值
     * @return int
     */
    public static int toInt(@Nullable final String str,final int defaultValue){
        if(StringUtil.isEmpty(str)){
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException exception){
            return defaultValue;
        }
    }
}
