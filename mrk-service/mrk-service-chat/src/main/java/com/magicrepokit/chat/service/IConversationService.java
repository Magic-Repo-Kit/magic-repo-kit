package com.magicrepokit.chat.service;

import com.magicrepokit.chat.dto.ChatMessagePagesDTO;

import java.io.InputStream;
import java.util.function.Consumer;

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
     * 聊天对话
     * @param token
     * @param messageId
     * @param content
     * @param conversationId
     * @param parentMessageId
     */
    void sendMsg(String token,String messageId, String content, String conversationId, String parentMessageId, Consumer<InputStream> streamProcessor);

    /**
     * 获取对话列表
     * @param offset
     * @param limit
     */
    ChatMessagePagesDTO conversationList(Integer offset, Integer limit);
}
