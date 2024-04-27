package com.magicrepokit.chat.vo.gptRole;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "Gpt角色列表返回实体",description = "Gpt角色列表返回")
public class GptRolePageVO {

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
}
