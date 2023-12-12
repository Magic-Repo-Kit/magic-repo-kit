package com.magicrepokit.mb.base;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.magicrepokit.common.utils.DateUtil;
import lombok.Data;


import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {
    /**
     * 主键id
     */

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 创建人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 状态[0:未删除,1:删除]
     */
    @TableLogic(delval = "1", value = "0")
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;
}
