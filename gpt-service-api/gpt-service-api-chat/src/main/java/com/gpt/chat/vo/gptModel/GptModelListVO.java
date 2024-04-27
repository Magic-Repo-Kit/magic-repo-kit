package com.gpt.chat.vo.gptModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "GptModelListVO", description = "GPT模型列表")
@AllArgsConstructor
@NoArgsConstructor
public class GptModelListVO {
    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "模型描述")
    private String modelDesc;

    public GptModelListVO (String modelName){
        this.modelName = modelName;
        this.modelDesc = modelName;
    }
}
