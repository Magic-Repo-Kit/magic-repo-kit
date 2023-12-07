package com.magicrepokit.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.magicrepokit.mp.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "mrk_gpt_conversation",autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GptConversation extends BaseEntity {
    /**
     * 会话id
     */
    private String conversationId;
    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 会话标题
     */
    private String title;

    /**
     * 会话状态[1:未完成 2:已完成]
     */
    private Integer status;
}
