package com.magicrepokit.chat.dto.knowledge;

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
@ApiModel(value = "知识库移动文件夹实体",description = "知识库移动文件")
public class KnowledgeMoveDTO {
    @ApiModelProperty(value = "知识库id")
    private Long id;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    private Long parentId;
}
