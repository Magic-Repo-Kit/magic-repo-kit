package com.magicrepokit.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.magicrepokit.mb.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "mrk_gpt_conversation_detail",autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(value = "会话详情",description = "会话详情")
public class GptConversationDetail extends BaseEntity {
    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    private String conversationId;

    /**
     * 消息id
     */
    @ApiModelProperty(value = "消息id")
    private String messageId;

    /**
     * 父消息id
     */
    @ApiModelProperty(value = "父消息id")
    private String parentMessageId;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 消息类型[1:提问 2:回答]
     */
    @ApiModelProperty(value = "消息类型[1:提问 2:回答]")
    private Integer type;

    /**
     * 消息状态[1:未完成 2:已完成]
     */
    @ApiModelProperty(value = "消息状态[1:未完成 2:已完成]")
    private Integer status;
}
