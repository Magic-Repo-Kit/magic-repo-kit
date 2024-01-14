package com.magicrepokit.chat.vo.gptRole;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.magicrepokit.chat.vo.knowledge.KnowledgeFileListVO;
import com.magicrepokit.chat.vo.knowledge.KnowledgeFileVO;
import com.magicrepokit.chat.vo.knowledge.KnowledgeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Gpt角色返回实体",description = "Gpt角色返回")
public class GptRoleVO {

    @ApiModelProperty(value = "角色id",required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 角色名称。
     */
    @ApiModelProperty(value = "角色名称")
    private String name;

    /**
     * 角色描述。
     */
    @ApiModelProperty(value = "角色描述")
    private String description;

    /**
     * 头像地址。
     */
    @ApiModelProperty(value = "头像地址")
    private String imageUrl;

    /**
     * 提示词prompt。
     */
    @ApiModelProperty(value = "提示词prompt")
    private String prompt;

    /**
     * 发散能力。
     */
    @ApiModelProperty(value = "发散能力(0~1)")
    private String temperature;

    /**
     * 模型名称。
     */
    @ApiModelProperty(value = "模型名称")
    private String modelName;

    /**
     * 预设对话。
     */
    @ApiModelProperty(value = "预设对话")
    private List<String> conversationStarters;

    /**
     * 插件列表。
     */
    @ApiModelProperty(value = "插件列表")
    private List<String> listPlugin;

    /**
     * 知识库id。
     */
    @ApiModelProperty(value = "知识库id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long knowledgeId;

    /**
     * 知识库信息
     */
    @ApiModelProperty(value = "知识库信息")
    private KnowledgeFileListVO knowledgeFileListVO;
}
