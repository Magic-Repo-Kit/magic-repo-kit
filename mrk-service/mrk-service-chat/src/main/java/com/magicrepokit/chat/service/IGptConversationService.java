package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.GptChatDTO;
import com.magicrepokit.chat.entity.GptConversation;
import com.magicrepokit.mp.base.BaseService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface IGptConversationService extends BaseService<GptConversation> {
    /**
     * 创建会话
     * @param userId 用户id
     * @param conversationId 会话id
     * @param title 会话标题
     * @return 是否成功
     */
    boolean addConversation(Long userId, String conversationId, String title);

    /**
     * 添加消息
     * @param conversationId 会话id
     * @param messageId 消息id
     * @param parentMessageId 父消息id
     * @param ask 问题
     * @param pastLine 上一句
     * @return 是否成功
     */
    boolean addMessage(String conversationId, String messageId, String parentMessageId, String ask, String pastLine);
}
