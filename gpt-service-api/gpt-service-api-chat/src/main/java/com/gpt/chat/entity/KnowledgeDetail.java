package com.gpt.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gpt.mb.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "gpt_knowledge_detail",autoResultMap = true)
@Data
@ApiModel(value = "知识库详情表",description = "知识库详情表")
public class KnowledgeDetail extends BaseEntity {
    /**
     * 知识库父级id
     */
    private Long knowledgeId;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件类型[text,md,pdf]
     */
    private String type;

    /**
     * 原始文件地址
     */
    private String fileUrl;

    /**
     * 任务状态[1:未开始 2:文件分隔中 3:训练 4:完成 5:失败]
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;
}
