package com.magicrepokit.chat.dto.gpt;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@ApiModel(value = "GptSaveChatMessage保存会话消息", description = "保存会话消息")
public class GptSaveChatMessage {
    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private List<MessageContext> messageContext;

    @Data
    @ApiModel(value = "MessageContext消息内容", description = "消息内容")
    public static class MessageContext{
        /**
         * 消息类型[1:用户 2:AI]
         */
        @ApiModelProperty(value = "消息类型[1:用户 2:AI]")
        private Integer type;
        /**
         * 消息内容
         */
        @ApiModelProperty(value = "消息内容")
        private String message;
        /**
         * 用户id
         */
        @ApiModelProperty(value = "用户id")
        private Long userId;
    }
}
