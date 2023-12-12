package com.magicrepokit.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.magicrepokit.mb.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "mrk_gpt_conversation_detail",autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GptConversationDetail extends BaseEntity {
    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 父消息id
     */
    private String parentMessageId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息类型[1:提问 2:回答]
     */
    private Integer type;

    /**
     * 消息状态[1:未完成 2:已完成]
     */
    private Integer status;
}
