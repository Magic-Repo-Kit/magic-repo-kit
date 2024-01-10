package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.gpt.GptChatDTO;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.mb.base.PageParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface IGptService {
    /**
     * gpt聊天
     * @param gptChatDTO 聊天内容
     * @return SseEmitter
     */
    SseEmitter chat(GptChatDTO gptChatDTO);

    /**
     * 知识库聊天
     * @param knowledgeId 知识库id
     * @param gptChatDTO 聊天内容
     * @return SseEmitter
     */
    SseEmitter chat(Long knowledgeId, GptChatDTO gptChatDTO);

    /**
     * 分页查询会话列表
     */
    PageResult<GptConversation> listConversationByPage(PageParam pageParam);

    /**
     * 查询会话详情
     * @param conversationId
     * @return
     */
    List<GptConversationDetail> listConversationDetail(String conversationId);

    PageResult<GptConversationDetail> listConversationDetailByPage(PageParam pageParam, String conversationId);


}
