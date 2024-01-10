package com.magicrepokit.chat.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel(value = "知识库目录路径实体",description = "知识库目录路径")
public class KnowledgePathVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "知识库父级id")
    private Long parentId;

    /**
     * 知识库名称
     */
    private String parentName;

}
