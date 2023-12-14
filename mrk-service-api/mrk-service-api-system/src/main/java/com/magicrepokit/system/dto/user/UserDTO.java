package com.magicrepokit.system.dto.user;

import com.magicrepokit.system.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    /**
     * 用户基本信息
     */
    private User user;
}
