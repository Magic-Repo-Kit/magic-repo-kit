package com.magicrepokit.chat.service;

import com.magicrepokit.chat.entity.ChatMessagePages;

public interface IConversationService {
    /**
     * 聊天对话
     * @param messageId
     * @param content
     * @param conversationId
     * @param parentMessageId
     */
    void sendMsg(String messageId,String content,String conversationId,String parentMessageId);

    /**
     * 获取对话列表
     * @param offset
     * @param limit
     */
    ChatMessagePages conversationList(Integer offset, Integer limit);
}
