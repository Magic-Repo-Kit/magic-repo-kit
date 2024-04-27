package com.magicrepokit.system.converter;

import com.magicrepokit.system.dto.auth.UserRegister;
import com.magicrepokit.system.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConverter {
    User registerDTO2User(UserRegister userRegister);
}
