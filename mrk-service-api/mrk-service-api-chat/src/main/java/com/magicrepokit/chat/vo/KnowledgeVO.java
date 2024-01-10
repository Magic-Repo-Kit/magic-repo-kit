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
@ApiModel(value = "知识库实体",description = "知识库")
public class KnowledgeVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "知识库id")
    private Long id;

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 级别类型[1:文件夹 2:文件]
     */
    private Integer type;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * els索引名称
     */
    private String indexName;
}
