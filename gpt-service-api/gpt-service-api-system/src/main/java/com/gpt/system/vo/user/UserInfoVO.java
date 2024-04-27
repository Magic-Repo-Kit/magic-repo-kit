package com.gpt.system.vo.user;

import com.gpt.system.entity.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户的基本信息
     */
    private User user;
}
