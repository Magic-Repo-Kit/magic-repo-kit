package com.magicrepokit.chat.vo.gpt;

import com.magicrepokit.chat.vo.Error;
import dev.langchain4j.data.segment.TextSegment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "GptSSEResponse",description = "gpt聊天返回")
public class GptSSEResponse {
    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    private String conversationId;

    /**
     * gpt角色id
     */
    @ApiModelProperty(value = "gpt角色id")
    private Long gptRoleId;

    /**
     * 聊天内容
     */
    @ApiModelProperty(value = "聊天内容")
    private String message;

    /**
     * 知识库内容
     */
    @ApiModelProperty(value = "知识库内容")
    private List<TextSegment> relevant;

    /**
     * 错误
     */
    @ApiModelProperty(value = "错误")
    private Error error;

    /**
     * 是否结束
     */
    @ApiModelProperty(value = "是否结束")
    private Boolean isEnd;
}
