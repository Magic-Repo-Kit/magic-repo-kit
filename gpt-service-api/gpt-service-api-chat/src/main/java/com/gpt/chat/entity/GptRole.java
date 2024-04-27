package com.gpt.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gpt.mb.base.BaseEntity;
import com.gpt.mb.type.StringListTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * gpt角色
 */

@Data
@TableName(value = "gpt_role",autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class GptRole extends BaseEntity {
    /**
     * 角色名称。
     */
    private String name;

    /**
     * 角色描述。
     */
    private String description;

    /**
     * 头像地址。
     */
    private String imageUrl;

    /**
     * 提示词prompt。
     */
    private String prompt;

    /**
     * 发散能力。
     */
    private String temperature;

    /**
     * 模型名称。
     */
    private String modelName;

    /**
     * 预设对话。
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> conversationStarters;

    /**
     * 知识库id。
     */
    private Long knowledgeId;

    /**
     * 是否展现知识库
     */
    private Integer isShowKnowledge;

    /**
     * 插件列表。
     */
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> listPlugin;
}
