package com.magicrepokit.chat.dto.knowledge;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "知识库更新实体",description = "知识库更新")
public class KnowledgeUpdateDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "id",required = true)
    private Long id;

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
     * 图片url
     */
    @ApiModelProperty(value = "图片url[只有文件类型才有图片url]")
    private String imageUrl;

    /**
     * 知识库文件匹配度[只有文件类型才有,默认0.7](0-1.0)
     */
    @ApiModelProperty(value = "知识库文件匹配度[只有文件类型才有,默认0.7](0-1.0)")
    private Double minScore;

    /**
     * 知识库文件匹配度[只有文件类型才有,默认5](0-20)
     */
    @ApiModelProperty(value = "知识库文件匹配度[只有文件类型才有,默认5](0-20)")
    private Integer maxResult;
}
