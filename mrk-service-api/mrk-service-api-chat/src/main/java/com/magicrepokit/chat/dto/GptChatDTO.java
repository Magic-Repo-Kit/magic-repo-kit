package com.magicrepokit.chat.dto;

import lombok.Data;

/**
 * 会话聊天模型
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Data
public class GptChatDTO {
    /**
     * 会话uuid
     */
    private String conversationId;

    /**
     * 消息uuid
     */
    private String messageId;

    /**
     * 父消息uuid
     */
    private String parentMessageId;

    /**
     * 聊天内容
     */
    private String content;
}
