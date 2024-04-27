package com.magicrepokit.system.service.impl;

import com.magicrepokit.mb.base.BaseServiceImpl;
import com.magicrepokit.system.entity.social.SocialUserBind;
import com.magicrepokit.system.mapper.SocialUserBindMapper;
import com.magicrepokit.system.service.ISocialUserBindService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SocialUserBindServiceImpl extends BaseServiceImpl<SocialUserBindMapper, SocialUserBind> implements ISocialUserBindService {
}
