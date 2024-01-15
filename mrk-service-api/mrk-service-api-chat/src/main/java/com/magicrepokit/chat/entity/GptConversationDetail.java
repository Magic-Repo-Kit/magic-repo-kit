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
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 用户类型[1:用户 2:AI]
     */
    @ApiModelProperty(value = "用户类型[1:用户 2:AI]")
    private Integer type;

    /**
     * 用户id[可以是用户id或者AI角色id]
     */
    @ApiModelProperty(value = "用户id[可以是用户id或者AI角色id]")
    private Long userId;
}
