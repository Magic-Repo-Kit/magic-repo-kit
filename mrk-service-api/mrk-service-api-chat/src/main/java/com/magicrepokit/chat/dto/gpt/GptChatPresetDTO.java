package com.magicrepokit.chat.dto.gpt;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import dev.langchain4j.data.message.ChatMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Gpt聊天预设实体", description = "Gpt聊天预设")
public class GptChatPresetDTO {
    @ApiModelProperty(value = "gpt模型名称", required = true)
    private String modelName;

    @ApiModelProperty(value = "发散能力(0~2)", required = true)
    private String temperature;

    @ApiModelProperty(value = "聊天记录", required = false)
    private List<GptChatMessage> messages;

    @ApiModelProperty(value = "提示词prompt", required = false)
    private String prompt;

    @ApiModelProperty(value = "知识库id", required = false)
    private Long knowledgeId;

    @ApiModelProperty(value = "结果是否展现知识库[1:关闭(默认) 2:开启]", required = false)
    private Integer isShowKnowledge;

    @ApiModelProperty(value = "是否联网[1:关闭(默认) 2:开启]", required = false)
    private Integer isOnline;


    public String getUserInput() {
        if(ObjectUtils.isNotEmpty(messages)){
            return messages.get(messages.size()-1).getMessage();
        }
        return null;
    }

    public List<ChatMessage> getChatMessages() {
        if(ObjectUtils.isNotEmpty(messages)){
            return messages.stream().map(GptChatMessage::toChatMessage).collect(java.util.stream.Collectors.toList());
        }
        return null;
    }

}
