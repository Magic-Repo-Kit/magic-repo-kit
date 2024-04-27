package com.gpt.chat.dto.gpt;

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
     * 角色id
     */
    @ApiModelProperty(value = "角色id",required = true)
    private Long roleId;

    /**
     * 会话uuid(新会话时为空)
     */
    @ApiModelProperty(value="会话id[uuid格式,不传开始新的会话，传入已存在的uuid继续上一次会话]",required = false)
    private String conversationId;

    /**
     * 聊天内容
     */
    @ApiModelProperty(value = "对话内容",required = true)
    private String content;

    /**
     * 是否联网[1:关闭 2:开启]
     */
    @ApiModelProperty(value = "是否联网[1:关闭 2:开启]",required = true)
    private Integer isOnline;

    /**
     * 是否开启上下文[1:关闭 2:开启]
     */
    @ApiModelProperty(value = "是否开启上下文[1:关闭 2:开启]",required = true)
    private Integer isContext;

    /**
     * 上下文长度问答对数量(只有开启上下文生效)[默认20，范围1-100]
     */
    @ApiModelProperty(value = "上下文长度问答对数量(只有开启上下文生效)[默认20，范围1-100]",required = false)
    private Integer contextLength;
}
