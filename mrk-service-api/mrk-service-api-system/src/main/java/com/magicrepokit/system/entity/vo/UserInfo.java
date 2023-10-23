package com.magicrepokit.system.entity.vo;

import com.magicrepokit.system.entity.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户的基本信息
     */
    private User user;
}
