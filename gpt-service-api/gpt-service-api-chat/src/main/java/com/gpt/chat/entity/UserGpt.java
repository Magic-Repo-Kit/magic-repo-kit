package com.gpt.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gpt.mb.base.BaseEntity;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_gpt",autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserGpt extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 普通额度
     */
    private Integer regularCreditLimit;

    /**
     * 订阅额度
     */
    private Integer subscriptionCreditLimit;

    /**
     * 订阅token
     */
    private String openToken;
}
