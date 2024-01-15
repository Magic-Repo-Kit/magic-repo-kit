package com.magicrepokit.chat.dto.gptRole;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Gpt角色更新实体",description = "Gpt角色更新")
public class GptRoleUpdateDTO {
    /**
     * Gpt角色id。
     */
    @ApiModelProperty(value = "Gpt角色id",required = true)
    private Long id;

    /**
     * 角色名称。
     */
    @ApiModelProperty(value = "角色名称",required = false)
    private String name;

    /**
     * 角色描述。
     */
    @ApiModelProperty(value = "角色描述",required = false)
    private String description;

    /**
     * 头像地址。
     */
    @ApiModelProperty(value = "头像地址",required = false)
    private String imageUrl;

    /**
     * 提示词prompt。
     */
    @ApiModelProperty(value = "提示词prompt",required = false)
    private String prompt;

    /**
     * 发散能力。
     */
    @ApiModelProperty(value = "发散能力(0~1)",required = false)
    private String temperature;

    /**
     * 模型名称。
     */
    @ApiModelProperty(value = "模型名称",required = false)
    private String modelName;

    /**
     * 预设对话。
     */
    @ApiModelProperty(value = "预设对话",required = false)
    private List<String> conversationStarters;

    /**
     * 知识库id。
     */
    @ApiModelProperty(value = "知识库id",required = false)
    private Long knowledgeId;

    /**
     * 插件列表。
     */
    @ApiModelProperty(value = "插件列表",required = false)
    private List<String> listPlugin;
}
