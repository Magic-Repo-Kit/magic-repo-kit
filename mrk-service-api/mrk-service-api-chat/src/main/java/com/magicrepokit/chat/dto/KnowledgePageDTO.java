package com.magicrepokit.chat.dto;

import com.magicrepokit.mb.base.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "知识库分页实体",description = "知识库分页")
public class KnowledgePageDTO extends PageParam {
    @ApiModelProperty(value = "知识库id")
    private Long id;
    @ApiModelProperty(value = "关键字")
    private String keywords;
}
