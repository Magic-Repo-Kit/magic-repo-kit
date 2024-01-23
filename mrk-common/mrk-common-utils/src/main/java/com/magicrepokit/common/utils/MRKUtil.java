package com.magicrepokit.common.utils;

import org.springframework.lang.Nullable;

import java.util.Random;

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

    /**
     * 生成5位邮箱验证码
     * @return 验证码
     */
    public static String emailVerificationCode() {
        return generateVerificationCode(5);
    }


    /**
     * 生成验证码
     * @param length 验证码长度
     * @return 验证码
     */
    private static String generateVerificationCode(int length) {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            codeBuilder.append(randomChar);
        }

        return codeBuilder.toString();
    }
}
