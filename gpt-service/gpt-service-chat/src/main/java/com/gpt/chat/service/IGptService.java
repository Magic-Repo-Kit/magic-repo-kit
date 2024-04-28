package com.gpt.chat.service;

import com.gpt.chat.dto.gpt.GptChatDTO;
import com.gpt.chat.dto.gpt.GptChatPresetDTO;
import com.gpt.chat.entity.GptConversationDetail;
import com.gpt.chat.vo.gpt.GptConversationPage;
import com.gpt.common.api.PageResult;
import com.gpt.mb.base.PageParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface IGptService {

    /**
     * gpt角色聊天
     * @param gptChatDTO 聊天内容
     * @return SseEmitter
     */
    SseEmitter chatRole(GptChatDTO gptChatDTO);

    /**
     * 分页查询会话列表
     * @param pageParam 分页参数
     */
    PageResult<GptConversationPage> pageConversation(PageParam pageParam);

    /**
     * 查询会话详情
     * @param conversationId 会话id
     * @return 会话详情
     */
    List<GptConversationDetail> listConversationDetail(String conversationId);

    /**
     * 分页查询会话详情
     * @param pageParam 分页参数
     * @param conversationId 会话id
     * @return 会话详情
     */

    PageResult<GptConversationDetail> listConversationDetailByPage(PageParam pageParam, String conversationId);
}
