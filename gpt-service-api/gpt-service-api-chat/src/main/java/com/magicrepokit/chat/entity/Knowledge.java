package com.magicrepokit.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.magicrepokit.mb.base.BaseEntity;
import com.magicrepokit.mb.type.LongListTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "mrk_gpt_knowledge",autoResultMap = true)
@Data
@ApiModel(value = "知识库管理表",description = "知识库管理表")
public class Knowledge extends BaseEntity {
    /**
     * 知识库父级id
     */
    private Long parentId;

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 级别类型[1:文件夹 2:文件]
     */
    private Integer type;

    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * els索引名称
     */
    private String indexName;

    /**
     * 知识库文件最小匹配度[只有文件类型才有,默认0.7](0-1.0)
     */
    private Double minScore;

    /**
     * 知识库文件返回搜索结果[只有文件类型才有,默认5](0-20)
     */
    private Integer maxResult;

    /**
     * 知识库路径
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> pathId;
}
