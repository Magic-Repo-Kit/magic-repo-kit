package com.gpt.system.service.impl;

import com.gpt.mb.base.BaseServiceImpl;
import com.gpt.system.entity.social.SocialUserBind;
import com.gpt.system.mapper.SocialUserBindMapper;
import com.gpt.system.service.ISocialUserBindService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SocialUserBindServiceImpl extends BaseServiceImpl<SocialUserBindMapper, SocialUserBind> implements ISocialUserBindService {
}
