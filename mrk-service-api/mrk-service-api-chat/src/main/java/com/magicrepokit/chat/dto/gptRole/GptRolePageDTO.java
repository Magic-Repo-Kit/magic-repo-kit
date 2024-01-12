package com.magicrepokit.chat.dto.gptRole;

import com.magicrepokit.mb.base.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Gpt角色分页实体",description = "Gpt角色分页")
public class GptRolePageDTO extends PageParam {
    @ApiModelProperty(value = "关键字[角色名称或描述]",required = false)
    private String keywords;

    /**
     * 模型名称。
     */
    @ApiModelProperty(value = "模型名称",required = false)
    private String modelName;
}
