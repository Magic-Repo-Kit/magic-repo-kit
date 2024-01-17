package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.gpt.GptChatDTO;
import com.magicrepokit.chat.dto.gpt.GptChatPresetDTO;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.vo.gpt.GptConversationPage;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.mb.base.PageParam;
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


    /**
     * gpt聊天预设
     * @param chatPresetDTO 聊天内容
     * @return SseEmitter
     */
    SseEmitter chatPreset(GptChatPresetDTO chatPresetDTO);
}
