package com.magicrepokit.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.magicrepokit.system.entity.SocialUserBind;

public interface SocialUserBindMapper extends BaseMapper<SocialUserBind> {
    default SocialUserBind selectBySocialUserId(Long socialUserId){
        return selectOne(new LambdaQueryWrapper<SocialUserBind>()
                .eq(SocialUserBind::getSocialUserId,socialUserId)
        );
    }
}
