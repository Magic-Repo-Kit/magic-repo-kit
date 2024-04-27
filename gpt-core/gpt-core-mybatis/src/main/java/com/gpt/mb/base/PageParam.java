package com.gpt.mb.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "分页参数",description = "分页参数")
public class PageParam {
    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    @ApiModelProperty(value = "页码[页码最小值为 1]",required = true)
    private Integer pageNo = PAGE_NO;

    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    @ApiModelProperty(value = "每条页数[每页条数最小值为 1,最大值为 100]",required = true)
    private Integer pageSize = PAGE_SIZE;
}
