package com.magicrepokit.langchain.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "向量数据实体类", description = "向量数据实体类")
public class Document {

    @ApiModelProperty(value = "向量数据集合")
    private float[] vector;
    @ApiModelProperty(value = "文本")
    private String text;
    @ApiModelProperty(value = "元数据")
    private Map<String, String> metadata;
}
