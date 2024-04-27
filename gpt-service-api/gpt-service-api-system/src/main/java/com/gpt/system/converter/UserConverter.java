package com.gpt.system.converter;

import com.gpt.system.dto.auth.UserRegister;
import com.gpt.system.entity.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConverter {
    User registerDTO2User(UserRegister userRegister);
}
