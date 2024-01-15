package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.gpt.GptConversationPageDTO;
import com.magicrepokit.chat.dto.gpt.GptSaveChatMessage;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.chat.entity.GptConversationDetail;
import com.magicrepokit.chat.vo.gpt.GptConversationPage;
import com.magicrepokit.common.api.PageResult;
import com.magicrepokit.mb.base.BaseService;
import com.magicrepokit.mb.base.PageParam;

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
     * 保存会话
     * @param gptSaveChatMessage
     * @return 保存结果
     */
    boolean saveConversation(GptSaveChatMessage gptSaveChatMessage);

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
    List<GptConversationDetail> listConversationHistory(String conversationId,String maxToken);
}
