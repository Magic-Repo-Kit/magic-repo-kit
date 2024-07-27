package com.magicrepokit.chat.service.tool;

import dev.langchain4j.agent.tool.Tool;

public class CalculatorService {
    /**
     * 计算两个数的和
     * @param a
     * @param b
     * @return
     */
    @Tool("计算两个数的和")
    public String sub(int a, int b) {
        System.out.println("Called add with a=" + a + ", b=" + b);
        return "a+b="+ (a + b);
    }

    @Tool("计算两个数的乘积")
    public String multi(int a, int b) {
        System.out.println("Called multi with a=" + a + ", b=" + b);
        return "axb="+ (a * b);
    }

    @Tool("默认空方函数")
    public String defaultMethod() {
        System.out.println("Called defaultMethod");
        return "默认空方函数";
    }

    @Tool("计算两个数的差除")
    public String div(int a, int b) {
        System.out.println("Called div with a=" + a + ", b=" + b);
        return "a/b="+ (a / b);
    }
}
