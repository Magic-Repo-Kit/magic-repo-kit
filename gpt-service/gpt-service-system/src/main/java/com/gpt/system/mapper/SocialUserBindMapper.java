package com.gpt.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gpt.system.entity.social.SocialUserBind;

public interface SocialUserBindMapper extends BaseMapper<SocialUserBind> {
    default SocialUserBind selectBySocialUserId(Long socialUserId){
        return selectOne(new LambdaQueryWrapper<SocialUserBind>()
                .eq(SocialUserBind::getSocialUserId,socialUserId)
        );
    }
}
