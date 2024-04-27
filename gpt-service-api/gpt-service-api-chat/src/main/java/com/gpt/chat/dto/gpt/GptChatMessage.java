package com.gpt.chat.dto.gpt;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GptChatMessage {
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

    public static ChatMessage toChatMessage(GptChatMessage gptChatMessage) {
        if(gptChatMessage.getType()==1){
            return new UserMessage(gptChatMessage.getMessage());
        }else{
            return new AiMessage(gptChatMessage.getMessage());
        }
    }
}
