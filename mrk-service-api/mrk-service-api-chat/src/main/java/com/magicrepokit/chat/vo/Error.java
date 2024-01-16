package com.magicrepokit.chat.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@ApiModel(value = "Error",description = "错误")
public class Error {
    /**
     * 错误码
     */
    @ApiModelProperty(value = "错误码")
    private String code;

    /**
     * 错误信息
     */
    @ApiModelProperty(value = "错误信息")
    private String message;
}
