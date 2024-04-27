package com.magicrepokit.chat.service.impl;

import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.mapper.GptConversationDetailMapper;
import com.magicrepokit.chat.service.IGptConversationDetailService;
import com.magicrepokit.mb.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GptConversationDetailServiceImpl extends BaseServiceImpl<GptConversationDetailMapper,GptConversationDetail> implements IGptConversationDetailService {
}
