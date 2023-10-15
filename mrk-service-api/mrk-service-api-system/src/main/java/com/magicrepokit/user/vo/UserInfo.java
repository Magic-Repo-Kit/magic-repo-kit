package com.magicrepokit.user.vo;

import com.magicrepokit.user.entity.User;
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
