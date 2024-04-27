package com.gpt.chat.vo.knowledge;

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
@ApiModel(value = "知识库分页",description = "分页")
public class KnowledgeListVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "知识库id")
    private Long id;

    /**
     * 父节点id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "知识库id")
    private Long parentId;

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
}
