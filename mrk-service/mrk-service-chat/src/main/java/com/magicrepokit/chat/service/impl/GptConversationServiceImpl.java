package com.magicrepokit.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.magicrepokit.chat.dto.GptConversationPageDTO;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.mapper.GptConversationMapper;
import com.magicrepokit.chat.service.IGptConversationDetailService;
import com.magicrepokit.chat.service.IGptConversationService;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.mp.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GptConversationServiceImpl extends BaseServiceImpl<GptConversationMapper, GptConversation> implements IGptConversationService {
    @Autowired
    private IGptConversationDetailService gptConversationDetailService;
    @Override
    public boolean addConversation(Long userId, String conversationId, String title) {
        GptConversation gptConversation = new GptConversation();
        gptConversation.setUserId(userId);
        gptConversation.setConversationId(conversationId);
        gptConversation.setTitle(title);
        gptConversation.setCreateTime(LocalDateTime.now());
        return this.save(gptConversation);
    }

    @Override
    public boolean addMessage(String conversationId, String messageId, String parentMessageId, String ask, String pastLine) {
        //1.创建提问
        LocalDateTime now = LocalDateTime.now();
        GptConversationDetail askDetail = new GptConversationDetail();
        askDetail.setConversationId(conversationId);
        askDetail.setMessageId(messageId);
        askDetail.setParentMessageId(parentMessageId);
        askDetail.setType(1);
        askDetail.setMessage(ask);
        askDetail.setCreateTime(now);
        GptConversationDetail contentDetail = new GptConversationDetail();
        contentDetail.setConversationId(conversationId);
        contentDetail.setMessageId(messageId);
        contentDetail.setParentMessageId(parentMessageId);
        contentDetail.setType(2);
        contentDetail.setMessage(pastLine);
        contentDetail.setCreateTime(now);
        //2.保存
        boolean askResult = gptConversationDetailService.save(askDetail);
        boolean contentResult = gptConversationDetailService.save(contentDetail);
        return askResult && contentResult;
    }

    @Override
    public PageResult<GptConversation> page(GptConversationPageDTO gptConversationPageDTO) {
        return selectPage(gptConversationPageDTO, new LambdaQueryWrapper<GptConversation>().eq(GptConversation::getUserId, gptConversationPageDTO.getUserId()));
    }

    @Override
    public List<GptConversationDetail> listConversationDetail(String conversationId) {
        return gptConversationDetailService.list(new LambdaQueryWrapper<GptConversationDetail>().eq(GptConversationDetail::getConversationId, conversationId)
                .orderByAsc(GptConversationDetail::getCreateTime));
    }
}
