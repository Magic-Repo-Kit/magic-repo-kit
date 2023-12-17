package com.magicrepokit.chat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会话聊天模型
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Data
@ApiModel(value = "会话聊天模型", description = "开始会话聊天参数")
public class GptChatDTO {
    /**
     * 会话uuid(新会话时为空)
     */
    @ApiModelProperty(value="会话id[uuid格式,不传开始新的会话，传入已存在的uuid继续上一次会话]")
    private String conversationId;

    /**
     * 消息uuid
     */
    @ApiModelProperty(value = "消息id[uuid格式，必传一个新的uuid]",required = true)
    private String messageId;

    /**
     * 父消息uuid
     */
    @ApiModelProperty(value = "父消息id[uuid格式，传上一个消息id,如果是第一个消息传入新的uuid]",required = true)
    private String parentMessageId;

    /**
     * 聊天内容
     */
    @ApiModelProperty(value = "对话内容",required = true)
    private String content;
}
