package com.magicrepokit.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    /**
     * 名
     */
    private String name;
    /**
     * 姓
     */
    private String surname;
}
