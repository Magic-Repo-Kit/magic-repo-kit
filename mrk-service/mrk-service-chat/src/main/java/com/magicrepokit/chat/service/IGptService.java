package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.GptChatDTO;
import com.magicrepokit.chat.dto.GptConversationPageDTO;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.mp.base.PageParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface IGptService {
    /**
     * gpt聊天
     * @param gptChatDTO
     * @return
     */
    SseEmitter chat(GptChatDTO gptChatDTO);

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
}
