package com.magicrepokit.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.magicrepokit.mb.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "mrk_gpt_conversation",autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(value = "会话列表",description = "会话列表")
public class GptConversation extends BaseEntity {
    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    private String conversationId;

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
}
