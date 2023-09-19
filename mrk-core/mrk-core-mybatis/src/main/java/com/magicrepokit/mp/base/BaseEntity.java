package com.magicrepokit.mp.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.magicrepokit.common.utils.DateUtil;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class BaseEntity {
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
    private Long createUser;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtil.PATTERN_DATETIME)
    private LocalDateTime updateTime;

    /**
     * 状态[0:未删除,1:删除]
     */
    private Integer deleteFlag;
}
