package com.magicrepokit.chat.dto;

import lombok.Data;

@Data
public class GptTokenGetDTO {
    /**
     * gpt token
     */
    private String token;

    /**
     * 普通额度
     */
    private Integer regularCreditLimit;

    /**
     * 订阅额度
     */
    private Integer subscriptionCreditLimit;

    /**
     * 状态[1:正常,2:无账户,3:无普通额度,4:无订阅额度,5:自己的账户]
     */
    private Integer status;
}
