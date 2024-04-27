package com.gpt.chat.service.impl;

import com.gpt.chat.entity.GptConversationDetail;
import com.gpt.chat.mapper.GptConversationDetailMapper;
import com.gpt.chat.service.IGptConversationDetailService;
import com.gpt.mb.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GptConversationDetailServiceImpl extends BaseServiceImpl<GptConversationDetailMapper,GptConversationDetail> implements IGptConversationDetailService {
}
