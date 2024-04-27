package com.gpt.chat.dto.knowledge;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@ApiModel(value = "知识库创建实体",description = "知识库创建")
public class KnowledgeCreateDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "父id")
    private Long parentId;

    /**
     * 知识库名称
     */
    @ApiModelProperty(value = "知识库名称")
    private String name;

    /**
     * 知识库描述(类型为文件夹不填)
     */
    @ApiModelProperty(value = "知识库描述")
    private String description;

    /**
     * 级别类型[1:文件夹 2:文件]
     */
    @ApiModelProperty(value = "级别类型[1:文件夹 2:文件]")
    private Integer type;

    /**
     * 图片url
     */
    @ApiModelProperty(value = "图片url[只有文件类型才有图片url]")
    private String imageUrl;

    /**
     * 知识库索引名称（必须英文全小写，空为自动生成）
     */
    @ApiModelProperty(value = "知识库索引名称[只有文件类型才有,必须英文全小写，空为自动生成]")
    private String indexName;

    /**
     * 知识库文件匹配度[只有文件类型才有,默认0.7](0-1.0)
     */
    @ApiModelProperty(value = "知识库文件匹配度[只有文件类型才有,默认0.7，范围0-1.0]")
    private Double minScore;

    /**
     * 知识库文件匹配度[只有文件类型才有,默认5](0-20)
     */
    @ApiModelProperty(value = "知识库文件匹配度[只有文件类型才有,默认5，范围0-20]")
    private Integer maxResult;
}
