package com.gpt.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gpt.system.entity.social.SocialUser;

public interface SocialUserMapper extends BaseMapper<SocialUser> {
    default SocialUser selectByTypeAndCodeAndCode(Integer type,String code,String state){
        return selectOne(new LambdaQueryWrapper<SocialUser>()
                .eq(SocialUser::getType, type)
                .eq(SocialUser::getCode, code)
                .eq(SocialUser::getState, state));
    }

    default SocialUser selectByOpenid(String openId){
        return selectOne(new LambdaQueryWrapper<SocialUser>()
                .eq(SocialUser::getOpenid,openId)
        );
    }
}
