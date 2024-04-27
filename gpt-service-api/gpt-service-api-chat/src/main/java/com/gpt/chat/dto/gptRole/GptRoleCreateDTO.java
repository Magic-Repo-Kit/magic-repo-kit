package com.gpt.chat.dto.gptRole;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Gpt角色创建实体",description = "Gpt角色创建")
public class GptRoleCreateDTO {
    /**
     * 角色名称。
     */
    @ApiModelProperty(value = "角色名称",required = true)
    private String name;

    /**
     * 头像地址。
     */
    @ApiModelProperty(value = "头像地址",required = false)
    private String imageUrl;

    /**
     * 提示词prompt。
     */
    @ApiModelProperty(value = "提示词prompt",required = true)
    private String prompt;

    /**
     * 发散能力。
     */
    @ApiModelProperty(value = "发散能力(0~2)",required = true)
    private String temperature;

    /**
     * 模型名称。
     */
    @ApiModelProperty(value = "模型名称",required = true)
    private String modelName;

    /**
     * 预设对话。
     */
    @ApiModelProperty(value = "预设对话",required = true)
    private List<String> conversationStarters;

    /**
     * 知识库id。
     */
    @ApiModelProperty(value = "知识库id",required = false)
    private Long knowledgeId;

    /**
     * 是否展现知识库
     */
    @ApiModelProperty(value = "结果是否展现知识库[1:关闭(默认) 2:开启]",required = false)
    private Integer isShowKnowledge;


    /**
     * 插件列表。
     */
    @ApiModelProperty(value = "插件列表",required = false)
    private List<String> listPlugin;
}
