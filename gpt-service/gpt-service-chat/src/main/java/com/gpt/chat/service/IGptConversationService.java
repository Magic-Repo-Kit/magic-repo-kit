package com.gpt.chat.service;

import com.gpt.chat.constant.GptModel;
import com.gpt.chat.dto.gpt.GptConversationPageDTO;
import com.gpt.chat.dto.gpt.GptSaveChatMessage;
import com.gpt.chat.entity.GptConversation;
import com.gpt.chat.entity.GptConversationDetail;
import com.gpt.chat.vo.gpt.GptConversationPage;
import com.gpt.common.api.PageResult;
import com.gpt.mb.base.BaseService;
import com.gpt.mb.base.PageParam;

import java.util.List;

public interface IGptConversationService extends BaseService<GptConversation> {
    PageResult<GptConversationPage> page(GptConversationPageDTO gptConversationPageDTO);

    /**
     * 获取会话详情
     * @param conversationId 会话id
     * @return 会话详情
     */
    List<GptConversationDetail> listConversationDetail(String conversationId);

    /**
     * 分页获取会话详情
     * @param pageParam
     * @param conversationId
     * @return
     */
    PageResult<GptConversationDetail> listConversationDetailByPage(PageParam pageParam, String conversationId);

    /**
     * 保存聊天记录
     * @param gptSaveChatMessage
     * @return 保存结果
     */
    boolean saveChatHistory(GptSaveChatMessage gptSaveChatMessage);

    /**
     * 新增会话
     */
    boolean addConversation(GptConversation gptConversation);

    /**
     * 获取聊天历史记录
     * @param conversationId 会话id
     * @param maxCount 最大条数
     */
    List<GptConversationDetail> listConversationHistory(String conversationId,Integer maxCount);

    /**
     * 获取聊天历史记录
     * @param conversationId 会话id
     * @param maxToken 最大token
     */
    List<GptConversationDetail> listConversationHistory(String conversationId, Integer maxToken, GptModel gptModel);
}
