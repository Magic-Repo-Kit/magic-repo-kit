package com.magicrepokit.chat.dto.knowledge;

import com.magicrepokit.mb.base.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "知识库分页实体",description = "知识库分页")
public class KnowledgeListDTO extends PageParam {
    @ApiModelProperty(value = "知识库父id",required = false)
    private Long parentId;
    @ApiModelProperty(value = "关键字",required = false)
    private String keywords;
    @ApiModelProperty(value = "级别类型[1:文件夹 2:文件]",required = false)
    private Integer type;
}
