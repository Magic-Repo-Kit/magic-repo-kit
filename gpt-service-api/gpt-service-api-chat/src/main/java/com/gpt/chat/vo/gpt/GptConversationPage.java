package com.gpt.chat.vo.gpt;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Gpt会话列表返回实体",description = "Gpt会话列表")
public class GptConversationPage {
    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    private String conversationId;

    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 会话标题
     */
    @ApiModelProperty(value = "会话标题")
    private String title;

    /**
     * GPT角色id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "GPT角色id")
    private Long gptRoleId;

    /**
     * GPT角色名称
     */
    @ApiModelProperty(value = "GPT角色名称")
    private String gptRoleName;

    /**
     * GPT角色头像
     */
    @ApiModelProperty(value = "GPT角色头像")
    private String gptRoleImageUrl;
}
