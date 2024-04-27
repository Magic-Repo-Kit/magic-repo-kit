package com.gpt.mb.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "租户基础实体类", description = "租户基础实体类")
public class TenantBaseEntity extends BaseEntity {
    /**
     * 多租户编号
     */
    @ApiModelProperty("多租户编号")
    private Long tenantId;
}
